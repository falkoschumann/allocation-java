package de.muspellheim.allocation.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muspellheim.allocation.domain.OrderLine;
import de.muspellheim.allocation.servicelayer.JpaUnitOfWork;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@Import(TestDbConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UowTests {
  @Autowired private EntityManagerFactory entityManagerFactory;

  @Test
  void uowCanRetrieveABatchAndAllocateToIt() {
    var entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    insertBatch(entityManager, "batch1", "HIPSTER-WORKBENCH", 100, null);
    entityManager.getTransaction().commit();
    var uow = new JpaUnitOfWork(entityManagerFactory);

    uow.with(
        () -> {
          var batch = uow.getBatches().get("batch1");
          var line = new OrderLine("o1", "HIPSTER-WORKBENCH", 10);
          batch.allocate(line);
          uow.commit();
        });

    var batchRef = getAllocatedBatchRef(entityManager, "o1", "HIPSTER-WORKBENCH");
    assertEquals("batch1", batchRef);
  }

  @Test
  void rollsBackUncommittedWorkByDefault() {
    var uow = new JpaUnitOfWork(entityManagerFactory);

    uow.with(() -> insertBatch(uow.getEntityManager(), "batch1", "MEDIUM-PLINTH", 100, null));

    var entityManager = entityManagerFactory.createEntityManager();
    var rows = entityManager.createNativeQuery("SELECT * FROM batches").getResultList();
    assertEquals(List.of(), rows);
  }

  @Test
  void rollsBackOnError() {
    var uow = new JpaUnitOfWork(entityManagerFactory);

    assertThrows(
        IllegalStateException.class,
        () ->
            uow.with(
                () -> {
                  insertBatch(uow.getEntityManager(), "batch1", "LARGE-FORK", 100, null);
                  throw new IllegalStateException("");
                }));

    var entityManager = entityManagerFactory.createEntityManager();
    var rows = entityManager.createNativeQuery("SELECT * FROM batches").getResultList();
    assertEquals(List.of(), rows);
  }

  private void insertBatch(
      EntityManager entityManager, String ref, String sku, int qty, @Nullable LocalDate eta) {
    entityManager
        .createNativeQuery(
            """
            INSERT INTO batches (reference, sku, purchased_quantity, eta)
            VALUES (:ref, :sku, :qty, :eta)
            """)
        .setParameter("ref", ref)
        .setParameter("sku", sku)
        .setParameter("qty", qty)
        .setParameter("eta", eta)
        .executeUpdate();
  }

  private String getAllocatedBatchRef(EntityManager entityManager, String orderId, String sku) {
    var orderLineId =
        (long)
            entityManager
                .createNativeQuery(
                    """
                      SELECT id
                        FROM order_lines
                       WHERE order_id=:order_id AND sku=:sku
                      """)
                .setParameter("order_id", orderId)
                .setParameter("sku", sku)
                .getSingleResult();
    return (String)
        entityManager
            .createNativeQuery(
                """
                SELECT b.reference
                  FROM allocations JOIN batches AS b ON batch_id = b.id
                 WHERE orderline_id=:order_line_id
                """)
            .setParameter("order_line_id", orderLineId)
            .getSingleResult();
  }
}
