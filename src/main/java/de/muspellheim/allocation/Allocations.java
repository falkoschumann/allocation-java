package de.muspellheim.allocation;

import java.util.List;

class Allocations {
  static String allocate(OrderLine line, List<Batch> batches) {
    var batch = batches.stream()
      .sorted()
      .filter(b -> b.canAllocate(line))
      .findFirst()
      .orElseThrow(() -> new OutOfStock("Out of stock for sku %s".formatted(line.sku())));
    batch.allocate(line);
    return batch.getReference();
  }
}
