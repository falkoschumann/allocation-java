/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "sku")
@ToString(of = "sku")
public class Product {

  private final String sku;
  private final List<Batch> batches;
  private int versionNumber;
  private final ArrayDeque<Event> events = new ArrayDeque<>();

  protected Product() {
    this("");
  }

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

  public Optional<String> allocate(OrderLine line) {
    Objects.requireNonNull(line, "The line cannot be null.");
    var optionalBatch = batches.stream().sorted().filter(b -> b.canAllocate(line)).findFirst();
    if (optionalBatch.isEmpty()) {
      events.offer(new OutOfStock(line.getSku()));
      return Optional.empty();
    }

    var batch = optionalBatch.get();
    batch.allocate(line);
    versionNumber += 1;
    return Optional.ofNullable(batch.getReference());
  }
}
