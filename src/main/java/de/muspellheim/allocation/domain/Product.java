/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "sku")
@ToString(of = "sku")
public class Product {
  @Getter private String sku = "";
  @Getter private List<Batch> batches = new ArrayList<>();
  @Getter private int versionNumber;

  public Product(String sku) {
    this(sku, new ArrayList<>());
  }

  public Product(String sku, List<Batch> batches) {
    this(sku, batches, 0);
  }

  public Product(String sku, List<Batch> batches, int versionNumber) {
    this.sku = sku;
    this.batches = batches;
    this.versionNumber = versionNumber;
  }

  public String allocate(OrderLine line) {
    var batch =
        batches.stream()
            .sorted()
            .filter(b -> b.canAllocate(line))
            .findFirst()
            .orElseThrow(() -> new OutOfStock("Out of stock for sku %s".formatted(line.getSku())));
    batch.allocate(line);
    versionNumber += 1;
    return batch.getReference();
  }
}
