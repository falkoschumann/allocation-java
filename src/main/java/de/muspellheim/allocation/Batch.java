package de.muspellheim.allocation;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.lang.Nullable;

public class Batch implements Comparable<Batch> {
  @Getter
  private final String reference;

  @Getter
  private final String sku;

  @Getter
  @Nullable
  private final LocalDate eta;

  @Getter(AccessLevel.MODULE)
  private final int purchasedQuantity;

  @Getter(AccessLevel.MODULE)
  private final Set<OrderLine> allocations;

  public Batch(String ref, String sku, int qty) {
    this(ref, sku, qty, null);
  }

  public Batch(String ref, String sku, int qty, @Nullable LocalDate eta) {
    this.reference = ref;
    this.sku = sku;
    this.eta = eta;
    this.purchasedQuantity = qty;
    this.allocations = new LinkedHashSet<>();
  }

  public int getAllocatedQuantity() {
    return allocations.stream()
      .mapToInt(OrderLine::qty)
      .sum();
  }

  public int getAvailableQuantity() {
    return purchasedQuantity - getAllocatedQuantity();
  }

  public void allocate(OrderLine line) {
    if (!canAllocate(line)) {
      return;
    }

    allocations.add(line);
  }

  public void deallocate(OrderLine line) {
    allocations.remove(line);
  }

  public boolean canAllocate(OrderLine line) {
    return sku.equals(line.sku()) && getAvailableQuantity() >= line.qty();
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

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Batch b)) {
      return false;
    }

    return reference.equals(b.reference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reference);
  }

  @Override
  public String toString() {
    return "Batch{" +
           "reference='" + reference + '\'' +
           '}';
  }
}
