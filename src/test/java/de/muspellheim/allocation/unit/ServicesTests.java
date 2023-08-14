/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.allocation.servicelayer.InvalidSku;
import de.muspellheim.allocation.servicelayer.Services;
import org.junit.jupiter.api.Test;

class ServicesTests {

  @Test
  void addBatch_ForNewProduct() {
    var uow = new FakeUnitOfWork();

    Services.addBatch("b1", "CRUNCHY-ARMCHAIR", 100, null, uow);

    assertTrue(uow.getProducts().get("CRUNCHY-ARMCHAIR").isPresent());
    assertTrue(uow.isCommitted());
  }

  @Test
  void addBatch_ForExistingProduct() {
    var uow = new FakeUnitOfWork();
    Services.addBatch("b1", "GARISH-RUG", 100, null, uow);

    Services.addBatch("b2", "GARISH-RUG", 99, null, uow);

    assertTrue(
        uow.getProducts().get("GARISH-RUG").orElseThrow().getBatches().stream()
            .anyMatch(b -> "b2".equals(b.getReference())));
  }

  @Test
  void allocate_ReturnsAllocation() {
    var uow = new FakeUnitOfWork();
    Services.addBatch("batch1", "COMPLICATED-LAMP", 100, null, uow);

    var result = Services.allocate("o1", "COMPLICATED-LAMP", 10, uow);

    assertEquals("batch1", result);
  }

  @Test
  void allocate_ErrorsForInvalidSku() {
    var uow = new FakeUnitOfWork();
    Services.addBatch("b1", "AREALSKU", 100, null, uow);

    var exception =
        assertThrows(InvalidSku.class, () -> Services.allocate("o1", "NONEXISTENTSKU", 10, uow));
    assertEquals("Invalid sku NONEXISTENTSKU", exception.getMessage());
  }

  @Test
  void allocate_Commits() {
    var uow = new FakeUnitOfWork();
    Services.addBatch("b1", "OMINOUS-MIRROR", 100, null, uow);

    Services.allocate("o1", "OMINOUS-MIRROR", 10, uow);

    assertTrue(uow.isCommitted());
  }
}
