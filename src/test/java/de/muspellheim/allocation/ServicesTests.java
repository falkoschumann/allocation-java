package de.muspellheim.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ServicesTests {
  @Test
  void returnsAllocation() {
    var line = new OrderLine("o1", "COMPLICATED-LAMP", 10);
    var batch = new Batch("b1", "COMPLICATED-LAMP", 100);
    var repo = new FakeRepository(batch);

    var result = Services.allocate(line, repo, new FakeEntityManager());

    assertEquals("b1", result);
  }

  @Test
  void errorForInvalidSku() {
    var line = new OrderLine("o1", "NONEXISTENTSKU", 10);
    var batch = new Batch("b1", "AREALSKU", 100);
    var repo = new FakeRepository(batch);

    var exception = assertThrows(InvalidSku.class, () -> Services.allocate(line, repo, new FakeEntityManager()));
    assertEquals("Invalid sku NONEXISTENTSKU", exception.getMessage());
  }

  @Test
  void commits() {
    var line = new OrderLine("o1", "OMINOUS-MIRROR", 10);
    var batch = new Batch("b1", "OMINOUS-MIRROR", 100);
    var repo = new FakeRepository(batch);
    var entityManager = new FakeEntityManager();

    Services.allocate(line, repo, entityManager);

    assertTrue(entityManager.isCommitted());
  }
}
