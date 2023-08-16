/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.integration;

import static de.muspellheim.allocation.RandomRefs.randomBatchRef;
import static de.muspellheim.allocation.RandomRefs.randomOrderId;
import static de.muspellheim.allocation.RandomRefs.randomSku;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.allocation.domain.OrderLine;
import de.muspellheim.allocation.servicelayer.JpaUnitOfWork;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UowTests {

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Test
  void uowCanRetrieveBatchAndAllocateToIt() {
    var entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    insertBatch(entityManager, "batch1", "HIPSTER-WORKBENCH", 100, null);
    entityManager.getTransaction().commit();
    var uow = new JpaUnitOfWork(entityManagerFactory);

    uow.with(
        () -> {
          var product = uow.getProducts().get("HIPSTER-WORKBENCH").orElseThrow();
          var line = new OrderLine("o1", "HIPSTER-WORKBENCH", 10);
          product.allocate(line);
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

  @Test
  void concurrentUpdatesToVersionAreNotAllowed() throws Exception {
    var sku = randomSku();
    var batch = randomBatchRef();
    var entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    insertBatch(entityManager, batch, sku, 100, null, 1);
    entityManager.getTransaction().commit();

    var order1 = randomOrderId("1");
    var order2 = randomOrderId("2");
    var exceptions = new ArrayList<Exception>();
    Runnable tryToAllocateOrder1 = () -> tryToAllocate(order1, sku, exceptions);
    Runnable tryToAllocateOrder2 = () -> tryToAllocate(order2, sku, exceptions);
    var thread1 = new Thread(tryToAllocateOrder1);
    var thread2 = new Thread(tryToAllocateOrder2);
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    var version =
        entityManager
            .createNativeQuery(
                """
                SELECT version_number
                  FROM products
                 WHERE sku=:sku
                """)
            .setParameter("sku", sku)
            .getSingleResult();
    assertEquals(2, version);
    assertEquals(1, exceptions.size());
    assertTrue(exceptions.get(0).getCause() instanceof OptimisticLockException);

    var orders =
        entityManager
            .createNativeQuery(
                """
                SELECT order_id FROM allocations
                  JOIN batches ON allocations.batch_id = batches.id
                  JOIN order_lines ON allocations.orderline_id = order_lines.id
                 WHERE order_lines.sku=:sku
                """)
            .setParameter("sku", sku)
            .getResultList();
    assertEquals(1, orders.size());
    var uow = new JpaUnitOfWork(entityManagerFactory);
    uow.with(() -> uow.getEntityManager().createNativeQuery("SELECT 1").getSingleResult());
  }

  private void insertBatch(
      EntityManager entityManager, String ref, String sku, int qty, LocalDate eta) {
    insertBatch(entityManager, ref, sku, qty, eta, 1);
  }

  private void insertBatch(
      EntityManager entityManager,
      String ref,
      String sku,
      int qty,
      LocalDate eta,
      int productVersion) {
    entityManager
        .createNativeQuery(
            """
            INSERT INTO products (sku, version_number)
            VALUES (:sku, :version)
            """)
        .setParameter("sku", sku)
        .setParameter("version", productVersion)
        .executeUpdate();
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

  private void tryToAllocate(String orderId, String sku, List<Exception> exceptions) {
    var line = new OrderLine(orderId, sku, 10);
    try {
      var uow = new JpaUnitOfWork(entityManagerFactory);
      uow.with(
          () -> {
            var product = uow.getProducts().get(sku).orElseThrow();
            product.allocate(line);
            try {
              Thread.sleep(200);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            uow.commit();
          });
    } catch (Exception e) {
      e.printStackTrace();
      exceptions.add(e);
    }
  }
}
