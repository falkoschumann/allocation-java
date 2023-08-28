/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.allocation.domain.Batch;
import de.muspellheim.allocation.domain.OrderLine;
import de.muspellheim.allocation.domain.OutOfStock;
import de.muspellheim.allocation.domain.Product;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductTests {

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
  void prefersWarehouseBatchesToShipment() {
    var inStockBatch = new Batch("in-stock-batch", "RETRO-CLOCK", 100);
    var shipmentBatch = new Batch("shipment-batch", "RETRO-CLOCK", 100, tomorrow);
    var product = new Product("RETRO-CLOCK", List.of(inStockBatch, shipmentBatch));
    var line = new OrderLine("oref", "RETRO-CLOCK", 10);

    product.allocate(line);

    assertEquals(90, inStockBatch.getAvailableQuantity());
    assertEquals(100, shipmentBatch.getAvailableQuantity());
  }

  @Test
  void prefersEarlierBatches() {
    var earliest = new Batch("speedy-batch", "MINIMALIST-SPOON", 100, today);
    var medium = new Batch("normal-batch", "MINIMALIST-SPOON", 100, tomorrow);
    var latest = new Batch("slow-batch", "MINIMALIST-SPOON", 100, later);
    var product = new Product("MINIMALIST-SPOON", List.of(medium, earliest, latest));
    var line = new OrderLine("order1", "MINIMALIST-SPOON", 10);

    product.allocate(line);

    assertEquals(90, earliest.getAvailableQuantity());
    assertEquals(100, medium.getAvailableQuantity());
    assertEquals(100, latest.getAvailableQuantity());
  }

  @Test
  void returnsAllocatedBatchRef() {
    var inStockBatch = new Batch("in-stock-batch-ref", "HIGHBROW-POSTER", 100);
    var shipmentBatch = new Batch("shipment-batch-ref", "HIGHBROW-POSTER", 100, tomorrow);
    var product = new Product("HIGHBROW-POSTER", List.of(inStockBatch, shipmentBatch));
    var line = new OrderLine("oref", "HIGHBROW-POSTER", 10);

    var allocation = product.allocate(line);

    assertTrue(allocation.isPresent());
    assertEquals(inStockBatch.getReference(), allocation.get());
  }

  @Test
  void recordsOutOfStockEventIfCannotAllocate() {
    var batch = new Batch("batch1", "SMALL-FORK", 10, today);
    var product = new Product("SMALL-FORK", List.of(batch));
    product.allocate(new OrderLine("order1", "SMALL-FORK", 10));

    var allocation = product.allocate(new OrderLine("order2", "SMALL-FORK", 1));

    assertEquals(new OutOfStock("SMALL-FORK"), product.getEvents().getLast());
    assertEquals(Optional.empty(), allocation);
  }

  @Test
  void incrementVersionNumber() {
    var line = new OrderLine("oref", "SCANDI-PEN", 10);
    var product = new Product("SCANDI-PEN", List.of(new Batch("b1", "SCANDI-PEN", 100)), 7);

    product.allocate(line);

    assertEquals(8, product.getVersionNumber());
  }
}
