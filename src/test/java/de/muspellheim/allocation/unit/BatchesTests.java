/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.allocation.domain.Batch;
import de.muspellheim.allocation.domain.OrderLine;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BatchesTests {

  @Test
  void allocatingToBatchReducesTheAvailableQuantity() {
    var batch = new Batch("batch-001", "SMALL-TABLE", 20, LocalDate.now());
    var line = new OrderLine("order-ref", "SMALL-TABLE", 2);

    batch.allocate(line);

    assertEquals(18, batch.getAvailableQuantity());
  }

  @Test
  void canAllocateIfAvailableGreaterThanRequired() {
    var largeBatch = newBatch("ELEGANT-LAMP", 20);
    var smallLine = newOrderLine("ELEGANT-LAMP", 2);

    var result = largeBatch.canAllocate(smallLine);

    assertTrue(result);
  }

  @Test
  void cannotAllocateIfAvailableSmallerThanRequired() {
    var smallBatch = newBatch("ELEGANT-LAMP", 2);
    var largeLine = newOrderLine("ELEGANT-LAMP", 20);

    var result = smallBatch.canAllocate(largeLine);

    assertFalse(result);
  }

  @Test
  void canAllocateIfAvailableEqualToRequired() {
    var largeBatch = newBatch("ELEGANT-LAMP", 2);
    var smallLine = newOrderLine("ELEGANT-LAMP", 2);

    var result = largeBatch.canAllocate(smallLine);

    assertTrue(result);
  }

  @Test
  void cannotAllocateIfSkusDoNotMatch() {
    var batch = new Batch("batch-001", "UNCOMFORTABLE-CHAIR", 100);
    var differentSkuLine = new OrderLine("order-123", "EXPENSIVE-TOASTER", 10);

    var result = batch.canAllocate(differentSkuLine);

    assertFalse(result);
  }

  @Test
  void allocationIsIdempotent() {
    var batch = newBatch("ANGULAR-DESK", 20);
    var line = newOrderLine("ANGULAR-DESK", 2);

    batch.allocate(line);
    batch.allocate(line);

    assertEquals(18, batch.getAvailableQuantity());
  }

  @Test
  void deallocate() {
    var batch = newBatch("EXPENSIVE-FOOTSTOOL", 20);
    var line = newOrderLine("EXPENSIVE-FOOTSTOOL", 2);
    batch.allocate(line);

    batch.deallocate(line);

    assertEquals(20, batch.getAvailableQuantity());
  }

  @Test
  void canOnlyDeallocateAllocatedLines() {
    var batch = newBatch("DECORATIVE-TRINKET", 20);
    var unallocatedLine = newOrderLine("DECORATIVE-TRINKET", 2);

    batch.deallocate(unallocatedLine);

    assertEquals(20, batch.getAvailableQuantity());
  }

  private static OrderLine newOrderLine(String sku, int lineQty) {
    return new OrderLine("order-123", sku, lineQty);
  }

  private static Batch newBatch(String sku, int batchQty) {
    return new Batch("batch-001", sku, batchQty, LocalDate.now());
  }
}
