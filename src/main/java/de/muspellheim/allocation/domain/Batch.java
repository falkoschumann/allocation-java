/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.domain;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "reference")
@ToString(of = "reference")
public class Batch implements Comparable<Batch> {

  @Setter(AccessLevel.PRIVATE)
  private Long id;

  private String reference = "";

  private String sku = "";

  private LocalDate eta;

  private int purchasedQuantity;

  private final Set<OrderLine> allocations = new LinkedHashSet<>();

  public Batch(String ref, String sku, int qty) {
    this(ref, sku, qty, null);
  }

  public Batch(String ref, String sku, int qty, LocalDate eta) {
    this.reference = Objects.requireNonNull(ref, "The ref cannot be null.");
    this.sku = Objects.requireNonNull(sku, "The sku cannot be null.");
    this.eta = eta;
    this.purchasedQuantity = qty;
  }

  public int getAllocatedQuantity() {
    return allocations.stream().mapToInt(OrderLine::getQty).sum();
  }

  public int getAvailableQuantity() {
    return purchasedQuantity - getAllocatedQuantity();
  }

  public boolean canAllocate(OrderLine line) {
    Objects.requireNonNull(line, "The line cannot be null.");
    return sku.equals(line.getSku()) && getAvailableQuantity() >= line.getQty();
  }

  public void allocate(OrderLine line) {
    if (canAllocate(line)) {
      allocations.add(line);
    }
  }

  public void deallocate(OrderLine line) {
    allocations.remove(line);
  }

  @Override
  public int compareTo(Batch other) {
    if (eta == null) {
      return -1;
    }
    if (other.eta == null) {
      return 1;
    }

    return eta.compareTo(other.eta);
  }
}
