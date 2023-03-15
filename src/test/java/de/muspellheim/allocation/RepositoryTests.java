package de.muspellheim.allocation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@Import(TestDbConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RepositoryTests {
  @Autowired private EntityManagerFactory entityManagerFactory;

  private EntityManager entityManager;

  @BeforeEach
  void init() {
    entityManager = entityManagerFactory.createEntityManager();
  }

  @Test
  void repositoryCanSaveABatch() {
    var batch = new Batch("batch1", "RUSTY-SOAPDISH", 100);
    var repo = new JpaRepository(entityManager);

    entityManager.getTransaction().begin();
    repo.add(batch);
    entityManager.getTransaction().commit();

    var rows =
        entityManager
            .createNativeQuery(
                """
        SELECT reference, sku, purchased_quantity, eta
          FROM batches
        """)
            .getResultList();
    assertEquals(1, rows.size());
    assertArrayEquals(new Object[] {"batch1", "RUSTY-SOAPDISH", 100, null}, (Object[]) rows.get(0));
  }

  private long insertOrderLine() {
    entityManager.getTransaction().begin();
    entityManager
        .createNativeQuery(
            """
        INSERT INTO order_lines (order_id, sku, qty)
        VALUES ('order1', 'GENERIC-SOFA', 12)
        """)
        .executeUpdate();
    entityManager.getTransaction().commit();
    var orderLineId =
        entityManager
            .createNativeQuery(
                """
          SELECT id
            FROM order_lines
           WHERE order_id=:order_id AND sku=:sku
          """)
            .setParameter("order_id", "order1")
            .setParameter("sku", "GENERIC-SOFA")
            .getSingleResult();
    return Long.parseLong(orderLineId.toString());
  }

  private long insertBatch(String reference) {
    entityManager.getTransaction().begin();
    entityManager
        .createNativeQuery(
            """
          INSERT INTO batches (reference, sku, purchased_quantity, eta)
          VALUES (:reference, 'GENERIC-SOFA', 100, null)
          """)
        .setParameter("reference", reference)
        .executeUpdate();
    entityManager.getTransaction().commit();
    var batchId =
        entityManager
            .createNativeQuery(
                """
          SELECT id
            FROM batches
           WHERE reference=:reference AND sku='GENERIC-SOFA'
          """)
            .setParameter("reference", reference)
            .getSingleResult();
    return Long.parseLong(batchId.toString());
  }

  private void insertAllocation(long orderLineId, long batchId) {
    entityManager.getTransaction().begin();
    entityManager
        .createNativeQuery(
            """
          INSERT INTO allocations (orderline_id, batch_id)
          VALUES (:orderline_id, :batch_id)
          """)
        .setParameter("orderline_id", orderLineId)
        .setParameter("batch_id", batchId)
        .executeUpdate();
    entityManager.getTransaction().commit();
  }

  @Test
  void repositoryCanRetrieveABatchWithAllocations() {
    var orderLineId = insertOrderLine();
    var batch1Id = insertBatch("batch2");
    insertBatch("batch3");
    insertAllocation(orderLineId, batch1Id);
    var repo = new JpaRepository(entityManager);

    var retrieved = repo.get("batch2");

    var expected = new Batch("batch2", "GENERIC-SOFA", 100);
    assertEquals(expected, retrieved); // equals only compares reference
    assertEquals(expected.getSku(), retrieved.getSku());
    assertEquals(expected.getPurchasedQuantity(), retrieved.getPurchasedQuantity());
    assertEquals(Set.of(new OrderLine("order1", "GENERIC-SOFA", 12)), retrieved.getAllocations());
  }
}
