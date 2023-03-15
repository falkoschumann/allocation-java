package de.muspellheim.allocation;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@NoArgsConstructor
public class Batch implements Comparable<Batch> {
  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PRIVATE)
  private Long id;

  @Getter
  private String reference = "";

  @Getter
  private String sku = "";

  @Getter
  @Nullable
  private LocalDate eta;

  @Getter(AccessLevel.PACKAGE)
  private int purchasedQuantity;

  @Getter(AccessLevel.PACKAGE)
  private Set<OrderLine> allocations = new LinkedHashSet<>();

  public Batch(String ref, String sku, int qty) {
    this(ref, sku, qty, null);
  }

  public Batch(String ref, String sku, int qty, @Nullable LocalDate eta) {
    this.reference = ref;
    this.sku = sku;
    this.eta = eta;
    this.purchasedQuantity = qty;
  }

  public int getAllocatedQuantity() {
    return allocations.stream()
      .mapToInt(OrderLine::getQty)
      .sum();
  }

  public int getAvailableQuantity() {
    return purchasedQuantity - getAllocatedQuantity();
  }

  public boolean canAllocate(OrderLine line) {
    return sku.equals(line.getSku()) && getAvailableQuantity() >= line.getQty();
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
    if (this == other) {
      return true;
    }
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
    return "Batch " + reference;
  }
}
