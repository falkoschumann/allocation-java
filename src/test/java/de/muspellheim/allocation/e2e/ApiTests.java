package de.muspellheim.allocation.e2e;

import static de.muspellheim.allocation.RandomRefs.randomBatchRef;
import static de.muspellheim.allocation.RandomRefs.randomOrderId;
import static de.muspellheim.allocation.RandomRefs.randomSku;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.allocation.Config;
import de.muspellheim.allocation.domain.OrderLine;
import de.muspellheim.allocation.entrypoints.AllocateResponse;
import de.muspellheim.allocation.entrypoints.BatchDto;
import de.muspellheim.allocation.entrypoints.ErrorMessageResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class ApiTests {
  @Autowired private Config config;

  private RestTemplate rest;

  @BeforeEach
  void init() throws Exception {
    rest = new RestTemplate();
    waitForWebAppToComeUp();
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

  @Test
  void happyPathReturns201AndAllocatedBatch() {
    var sku = randomSku();
    var otherSku = randomSku("other");
    var earlyBatch = randomBatchRef("1");
    var laterBatch = randomBatchRef("2");
    var otherBatch = randomBatchRef("3");
    postToAddBatch(laterBatch, sku, 100, LocalDate.parse("2011-01-02"));
    postToAddBatch(earlyBatch, sku, 100, LocalDate.parse("2011-01-01"));
    postToAddBatch(otherBatch, otherSku, 100, null);
    var data = new OrderLine(randomOrderId(), sku, 3);
    var url = config.getApiUrl();

    var response = rest.postForEntity("%s/allocate".formatted(url), data, AllocateResponse.class);

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
            () -> rest.postForEntity("%s/allocate".formatted(url), data, AllocateResponse.class));

    assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    Assertions.assertEquals(
        new ErrorMessageResponse("Invalid sku %1$s".formatted(unknownSku)),
        response.getResponseBodyAs(ErrorMessageResponse.class));
  }

  private void postToAddBatch(String ref, String sku, int qty, @Nullable LocalDate eta) {
    var url = config.getApiUrl();

    var batch = new BatchDto(ref, sku, qty, eta);
    var response = rest.postForEntity("%s/add-batch".formatted(url), batch, AllocateResponse.class);

    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
  }
}
