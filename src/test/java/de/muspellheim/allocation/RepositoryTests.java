package de.muspellheim.allocation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RepositoryTests {
  @Autowired
  private EntityManagerFactory entityManagerFactory;

  private EntityManager entityManager;

  @BeforeEach
  void init() {
    entityManager = entityManagerFactory.createEntityManager();
  }

  @Test
  void repositoryCanSaveABatch() {
    var batch = new Batch("batch1", "RUSTY-SOAPDISH", 100);
    var repo = new SqlRepository(entityManager);

    entityManager.getTransaction().begin();
    repo.add(batch);
    entityManager.getTransaction().commit();

    var rows = entityManager.createNativeQuery(
      """
        SELECT reference, sku, purchased_quantity, eta
          FROM batches
        """
    ).getResultList();
    assertEquals(1, rows.size(), "number of rows");
    assertArrayEquals(
      new Object[]{"batch1", "RUSTY-SOAPDISH", 100, null},
      (Object[]) rows.get(0),
      "columns of row"
    );
  }

  private long insertOrderLine() {
    entityManager.getTransaction().begin();
    entityManager.createNativeQuery(
      """
        INSERT INTO order_lines (id, order_id, sku, qty)
        VALUES (NEXT VALUE FOR order_lines_seq, 'order1', 'GENERIC-SOFA', 12)
        """).executeUpdate();
    entityManager.getTransaction().commit();
    var orderLineId = entityManager.createNativeQuery(
        """
          SELECT id
            FROM order_lines
           WHERE order_id=:orderId AND sku=:sku
          """
      ).setParameter("orderId", "order1")
      .setParameter("sku", "GENERIC-SOFA")
      .getSingleResult();
    return Long.parseLong(orderLineId.toString());
  }

  private long insertBatch(String reference) {
    entityManager.getTransaction().begin();
    entityManager.createNativeQuery(
        """
          INSERT INTO batches (id, reference, sku, purchased_quantity, eta)
          VALUES (NEXT VALUE FOR batches_seq, :reference, 'GENERIC-SOFA', 100, null)
          """)
      .setParameter("reference", reference)
      .executeUpdate();
    entityManager.getTransaction().commit();
    var batchId = entityManager.createNativeQuery(
        """
          SELECT id
            FROM batches
           WHERE reference=:reference AND sku='GENERIC-SOFA'
          """
      ).setParameter("reference", reference)
      .getSingleResult();
    return Long.parseLong(batchId.toString());
  }

  private void insertAllocation(long orderLineId, long batchId) {
    entityManager.getTransaction().begin();
    entityManager.createNativeQuery(
        """
          INSERT INTO allocations (id, orderline_id, batch_id)
          VALUES (NEXT VALUE FOR allocations_seq, :orderLineId, :batchId)
          """).setParameter("orderLineId", orderLineId)
      .setParameter("batchId", batchId)
      .executeUpdate();
    entityManager.getTransaction().commit();
  }

  @Test
  void repositoryCanRetrieveABatchWithAllocations() {
    var orderLineId = insertOrderLine();
    var batch1Id = insertBatch("batch2");
    insertBatch("batch3");
    insertAllocation(orderLineId, batch1Id);
    var repo = new SqlRepository(entityManager);

    var retrieved = repo.get("batch2");

    var expected = new Batch("batch2", "GENERIC-SOFA", 100);
    assertEquals(expected, retrieved, "equals only compares reference");
    assertEquals(expected.getSku(), retrieved.getSku(), "SKU");
    assertEquals(expected.getSku(), retrieved.getSku(), "purchased quantity");
    assertEquals(expected.getSku(), retrieved.getSku(), "allocations");
  }
}
