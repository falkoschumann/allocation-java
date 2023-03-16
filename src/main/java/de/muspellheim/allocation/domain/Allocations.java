package de.muspellheim.allocation.domain;

import java.util.List;

public class Allocations {
  public static String allocate(OrderLine line, List<Batch> batches) {
    var batch =
        batches.stream()
            .sorted()
            .filter(b -> b.canAllocate(line))
            .findFirst()
            .orElseThrow(() -> new OutOfStock("Out of stock for sku %s".formatted(line.getSku())));
    batch.allocate(line);
    return batch.getReference();
  }
}
