/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "sku")
@ToString(of = "sku")
public class Product {

  private String sku = "";
  private List<Batch> batches = new ArrayList<>();
  private int versionNumber;

  public Product(String sku) {
    this(sku, new ArrayList<>());
  }

  public Product(String sku, List<Batch> batches) {
    this(sku, batches, 0);
  }

  public Product(String sku, List<Batch> batches, int versionNumber) {
    this.sku = Objects.requireNonNull(sku, "The sku cannot be null.");
    this.batches = Objects.requireNonNull(batches, "The batches cannot be null.");
    this.versionNumber = versionNumber;
  }

  public String allocate(OrderLine line) {
    Objects.requireNonNull(line, "The line cannot be null.");
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
