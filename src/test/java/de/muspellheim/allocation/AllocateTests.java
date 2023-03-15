package de.muspellheim.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AllocateTests {
  private LocalDate today;
  private LocalDate tomorrow;
  private LocalDate later;

  @BeforeEach
  void init() {
    today = LocalDate.now();
    tomorrow = today.plusDays(1);
    later = today.plusDays(10);
  }

  @Test
  void prefersCurrentStockBatchesToShipment() {
    var inStockBatch = new Batch("in-stock-batch", "RETRO-CLOCK", 100);
    var shipmentBatch = new Batch("shipment-batch", "RETRO-CLOCK", 100, tomorrow);
    var line = new OrderLine("oref", "RETRO-CLOCK", 10);

    Allocations.allocate(line, List.of(inStockBatch, shipmentBatch));

    assertEquals(90, inStockBatch.getAvailableQuantity());
    assertEquals(100, shipmentBatch.getAvailableQuantity());
  }

  @Test
  void prefersEarlierBatches() {
    var earliest = new Batch("speedy-batch", "MINIMALIST-SPOON", 100, today);
    var medium = new Batch("normal-batch", "MINIMALIST-SPOON", 100, tomorrow);
    var latest = new Batch("slow-batch", "MINIMALIST-SPOON", 100, later);
    var line = new OrderLine("order1", "MINIMALIST-SPOON", 10);

    Allocations.allocate(line, List.of(medium, earliest, latest));

    assertEquals(90, earliest.getAvailableQuantity());
    assertEquals(100, medium.getAvailableQuantity());
    assertEquals(100, latest.getAvailableQuantity());
  }

  @Test
  void returnsAllocatedBatchRef() {
    var inStockBatch = new Batch("in-stock-batch-ref", "HIGHBROW-POSTER", 100);
    var shipmentBatch = new Batch("shipment-batch-ref", "HIGHBROW-POSTER", 100, tomorrow);
    var line = new OrderLine("oref", "HIGHBROW-POSTER", 10);

    var allocation = Allocations.allocate(line, List.of(inStockBatch, shipmentBatch));

    assertEquals(inStockBatch.getReference(), allocation);
  }

  @Test
  void throwsOutOfStockExceptionIfCannotAllocate() {
    var batch = new Batch("batch1", "SMALL-FORK", 10, today);
    Allocations.allocate(new OrderLine("order1", "SMALL-FORK", 10), List.of(batch));

    assertThrows(OutOfStock.class, () -> Allocations.allocate(new OrderLine("order2", "SMALL-FORK", 1), List.of(batch)));
  }
}
