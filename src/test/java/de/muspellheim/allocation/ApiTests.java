package de.muspellheim.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class ApiTests {
  @Autowired private Config config;

  @Autowired private EntityManagerFactory entityManagerFactory;

  private EntityManager entityManager;

  private RestTemplate rest;

  private Set<Long> batchesAdded;
  private Set<String> skusAdded;

  @BeforeEach
  void init() throws Exception {
    rest = new RestTemplate();
    entityManager = entityManagerFactory.createEntityManager();
    batchesAdded = new LinkedHashSet<>();
    skusAdded = new LinkedHashSet<>();

    waitForWebAppToComeUp();
  }

  @Test
  void happyPathReturns201AndAllocatedBatch() {
    var sku = randomSku();
    var otherSku = randomSku("other");
    var earlyBatch = randomBatchref("1");
    var laterBatch = randomBatchref("2");
    var otherBatch = randomBatchref("3");
    addStock(
        List.of(
            new StockItem(laterBatch, sku, 100, LocalDate.parse("2011-01-02")),
            new StockItem(earlyBatch, sku, 100, LocalDate.parse("2011-01-01")),
            new StockItem(otherBatch, otherSku, 100, null)));
    var data = new OrderLine(randomOrderId(), sku, 3);
    var url = config.getApiUrl();

    var response = rest.postForEntity("%1$s/allocate".formatted(url), data, AllocateResponse.class);

    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
    assertEquals(new AllocateResponse(earlyBatch), response.getBody());
  }

  @Test
  void unhappyPathReturns400AndErrorMessage() {
    var unknownSku = randomSku();
    var orderId = randomOrderId();
    var data = new OrderLine(orderId, unknownSku, 20);
    var url = config.getApiUrl();

    var response =
        assertThrows(
            HttpClientErrorException.class,
            () -> rest.postForEntity("%1$s/allocate".formatted(url), data, AllocateResponse.class));

    assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    assertEquals(
        new ErrorMessageResponse("Invalid sku %1$s".formatted(unknownSku)),
        response.getResponseBodyAs(ErrorMessageResponse.class));
  }

  @AfterEach
  void tearDown() {
    entityManager.getTransaction().begin();
    for (var batchId : batchesAdded) {
      entityManager
          .createNativeQuery(
              """
          DELETE FROM allocations
           WHERE batch_id=:batch_id
          """)
          .setParameter("batch_id", batchId)
          .executeUpdate();
      entityManager
          .createNativeQuery(
              """
          DELETE FROM batches
           WHERE id=:batch_id
          """)
          .setParameter("batch_id", batchId)
          .executeUpdate();
    }
    for (var sku : skusAdded) {
      entityManager
          .createNativeQuery(
              """
          DELETE FROM order_lines
           WHERE sku=:sku
          """)
          .setParameter("sku", sku)
          .executeUpdate();
    }
    entityManager.getTransaction().commit();
  }

  private void addStock(List<StockItem> lines) {
    for (var batch : lines) {
      entityManager.getTransaction().begin();
      entityManager
          .createNativeQuery(
              """
                INSERT INTO batches (reference, sku, purchased_quantity, eta)
                VALUES (:ref, :sku, :qty, :eta)
                """)
          .setParameter("ref", batch.ref())
          .setParameter("sku", batch.sku())
          .setParameter("qty", batch.qty())
          .setParameter("eta", batch.eta())
          .executeUpdate();
      entityManager.getTransaction().commit();
      var batchId =
          entityManager
              .createNativeQuery(
                  """
          SELECT id
            FROM batches
           WHERE reference=:ref AND sku=:sku
          """)
              .setParameter("ref", batch.ref())
              .setParameter("sku", batch.sku())
              .getSingleResult();
      batchesAdded.add((long) batchId);
      skusAdded.add(batch.sku());
    }
  }

  private static String randomSku() {
    return randomSku("");
  }

  private static String randomSku(String name) {
    return "sku-%1$s-%2$s".formatted(name, randomSuffix());
  }

  private static String randomBatchref() {
    return randomBatchref("");
  }

  private static String randomBatchref(String name) {
    return "batch-%1$s-%2$s".formatted(name, randomSuffix());
  }

  private static String randomOrderId() {
    return randomOrderId("");
  }

  private static String randomOrderId(String name) {
    return "order-%1$s-%2$s".formatted(name, randomSuffix());
  }

  private static String randomSuffix() {
    return UUID.randomUUID().toString().substring(0, 6);
  }

  private void waitForWebAppToComeUp() throws InterruptedException {
    var deadline = System.currentTimeMillis() + 10_000;
    var url = config.getApiUrl();
    while (System.currentTimeMillis() < deadline) {
      try {
        rest.getForObject(url, String.class);
        break;
      } catch (RestClientException e) {
        Thread.sleep(500);
      }
    }
  }
}
