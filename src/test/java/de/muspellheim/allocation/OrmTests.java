package de.muspellheim.allocation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;
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
public class OrmTests {
  @Autowired private EntityManagerFactory entityManagerFactory;

  private EntityManager entityManager;

  @BeforeEach
  void init() {
    entityManager = entityManagerFactory.createEntityManager();
  }

  @Test
  void orderLineMapperCanLoadLines() {
    entityManager.getTransaction().begin();
    entityManager
        .createNativeQuery(
            """
        INSERT INTO order_lines (order_id, sku, qty)
        VALUES ('order1', 'RED-CHAIR', 12),
               ('order1', 'RED-TABLE', 13),
               ('order2', 'BLUE-LIPSTICK', 14)
        """)
        .executeUpdate();
    entityManager.getTransaction().commit();

    var result = entityManager.createQuery("from OrderLine", OrderLine.class).getResultList();

    var expected =
        List.of(
            new OrderLine("order1", "RED-CHAIR", 12),
            new OrderLine("order1", "RED-TABLE", 13),
            new OrderLine("order2", "BLUE-LIPSTICK", 14));
    assertEquals(expected, result);
  }

  @Test
  void orderLineMapperCanSaveLines() {
    entityManager.getTransaction().begin();
    var newLine = new OrderLine("order1", "DECORATIVE-WIDGET", 12);
    entityManager.persist(newLine);
    entityManager.getTransaction().commit();

    var row =
        entityManager
            .createNativeQuery(
                """
        SELECT order_id, sku, qty
          FROM order_lines
         WHERE order_id='order1'
        """)
            .getSingleResult();
    assertArrayEquals(new Object[] {"order1", "DECORATIVE-WIDGET", 12}, (Object[]) row);
  }

  @Test
  void retrievingBatches() {
    entityManager.getTransaction().begin();
    entityManager
        .createNativeQuery(
            """
        INSERT INTO batches (reference, sku, purchased_quantity, eta)
        VALUES ('batch1', 'sku1', 100, null),
               ('batch2', 'sku2', 200, '2011-04-11')
        """)
        .executeUpdate();
    entityManager.getTransaction().commit();

    var result = entityManager.createQuery("from Batch", Batch.class).getResultList();

    var expected =
        List.of(
            new Batch("batch1", "sku1", 100),
            new Batch("batch2", "sku2", 200, LocalDate.of(2011, 4, 11)));
    assertEquals(expected, result);
  }

  @Test
  void savingBatches() {
    entityManager.getTransaction().begin();
    var batch = new Batch("batch1", "sku1", 100);
    entityManager.persist(batch);
    entityManager.getTransaction().commit();

    var row =
        entityManager
            .createNativeQuery(
                """
        SELECT reference, sku, purchased_quantity, eta
          FROM batches
        """)
            .getSingleResult();
    assertArrayEquals(new Object[] {"batch1", "sku1", 100, null}, (Object[]) row);
  }

  @Test
  void savingAllocations() {
    entityManager.getTransaction().begin();
    var batch = new Batch("batch1", "sku1", 100);
    var line = new OrderLine("order1", "sku1", 10);
    batch.allocate(line);
    entityManager.persist(line);
    entityManager.persist(batch);
    entityManager.getTransaction().commit();

    var row =
        entityManager
            .createNativeQuery(
                """
        SELECT orderline_id, batch_id
          FROM allocations
        """)
            .getSingleResult();
    assertArrayEquals(new Object[] {batch.getId(), line.getId()}, (Object[]) row);
  }

  @Test
  void retrievingAllocations() {
    entityManager.getTransaction().begin();
    entityManager
        .createNativeQuery(
            """
        INSERT INTO order_lines (order_id, sku, qty)
        VALUES ('order1', 'sku1', 12)
        """)
        .executeUpdate();
    var olid =
        entityManager
            .createNativeQuery(
                """
          SELECT id FROM order_lines
           WHERE order_id=:order_id AND sku=:sku
          """)
            .setParameter("order_id", "order1")
            .setParameter("sku", "sku1")
            .getSingleResult();
    entityManager
        .createNativeQuery(
            """
        INSERT INTO batches (reference, sku, purchased_quantity, eta)
        VALUES ('batch1', 'sku1', 100, null)
        """)
        .executeUpdate();
    var bid =
        entityManager
            .createNativeQuery(
                """
          SELECT id FROM batches
           WHERE reference=:ref AND sku=:sku
          """)
            .setParameter("ref", "batch1")
            .setParameter("sku", "sku1")
            .getSingleResult();
    entityManager
        .createNativeQuery(
            """
          INSERT INTO allocations (orderline_id, batch_id)
          VALUES (:olid, :bid)
          """)
        .setParameter("olid", olid)
        .setParameter("bid", bid)
        .executeUpdate();
    entityManager.getTransaction().commit();

    var batch = entityManager.createQuery("from Batch", Batch.class).getSingleResult();

    assertEquals(Set.of(new OrderLine("order1", "sku1", 12)), batch.getAllocations());
  }
}
