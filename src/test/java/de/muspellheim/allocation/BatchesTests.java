package de.muspellheim.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BatchesTests {
  @Test
  void allocatingToABatchReducesTheAvailableQuantity() {
    var batch = new Batch("batch-001", "SMALL-TABLE", 20, LocalDate.now());
    var line = new OrderLine("order-ref", "SMALL-TABLE", 2);

    batch.allocate(line);

    assertEquals(18, batch.getAvailableQuantity(), "batch available quantity");
  }

  Tuple<Batch, OrderLine> makeBatchAndLine(String sku, int batchQty, int lineQty) {
    return new Tuple<>(
      new Batch("batch-001", sku, batchQty, LocalDate.now()),
      new OrderLine("order-123", sku, lineQty)
    );
  }

  @Test
  void canAllocateIfAvailableGreaterThanRequired() {
    var batchAndLine = makeBatchAndLine("ELEGANT-LAMP", 20, 2);
    var largeBatch = batchAndLine.first();
    var smallLine = batchAndLine.second();

    assertTrue(largeBatch.canAllocate(smallLine), "large batch can allocate small line");
  }

  @Test
  void cannotAllocateIfAvailableSmallerThanRequired() {
    var batchAndLine = makeBatchAndLine("ELEGANT-LAMP", 2, 20);
    var smallBatch = batchAndLine.first();
    var largeLine = batchAndLine.second();

    assertFalse(smallBatch.canAllocate(largeLine), "small batch cannot allocate large line");
  }

  @Test
  void canAllocateIfAvailableEqualToRequired() {
    var batchAndLine = makeBatchAndLine("ELEGANT-LAMP", 2, 2);
    var largeBatch = batchAndLine.first();
    var smallLine = batchAndLine.second();

    assertTrue(largeBatch.canAllocate(smallLine), "large batch can allocate small line");
  }

  @Test
  void cannotAllocateIfSkusDoNotMatch() {
    var batch = new Batch("batch-001", "UNCOMFORTABLE-CHAIR", 100);
    var differentSkuLine = new OrderLine("order-123", "EXPENSIVE-TOASTER", 10);

    assertFalse(batch.canAllocate(differentSkuLine), "batch cannot allocate differente SKU line");
  }

  @Test
  void deallocate() {
    var batchAndLine = makeBatchAndLine("EXPENSIVE-FOOTSTOOL", 20, 2);
    var batch = batchAndLine.first();
    var line = batchAndLine.second();
    batch.allocate(line);

    batch.deallocate(line);

    assertEquals(20, batch.getAvailableQuantity(), "batch available quantity");
  }

  @Test
  void canOnlyDeallocateAllocatedLines() {
    var batchAndLine = makeBatchAndLine("DECORATIVE-TRINKET", 20, 2);
    var batch = batchAndLine.first();
    var unallocatedLine = batchAndLine.second();

    batch.deallocate(unallocatedLine);

    assertEquals(20, batch.getAvailableQuantity(), "batch available quantity");
  }

  @Test
  void allocationIsIdempotent() {
    var batchAndLine = makeBatchAndLine("ANGULAR-DESK", 20, 2);
    var batch = batchAndLine.first();
    var line = batchAndLine.second();

    batch.allocate(line);
    batch.allocate(line);

    assertEquals(18, batch.getAvailableQuantity(), "batch available quantity");
  }
}
