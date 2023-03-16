package de.muspellheim.allocation.domain;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@EqualsAndHashCode(of = "reference")
@ToString(of = "reference")
public class Batch implements Comparable<Batch> {
  @Getter
  @Setter(AccessLevel.PRIVATE)
  private Long id;

  @Getter private String reference = "";

  @Getter private String sku = "";

  @Getter @Nullable private LocalDate eta;

  @Getter private int purchasedQuantity;

  @Getter private final Set<OrderLine> allocations = new LinkedHashSet<>();

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
    return allocations.stream().mapToInt(OrderLine::getQty).sum();
  }

  public int getAvailableQuantity() {
    return purchasedQuantity - getAllocatedQuantity();
  }

  public boolean canAllocate(OrderLine line) {
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
  public int compareTo(@NonNull Batch other) {
    if (eta == null) {
      return -1;
    }
    if (other.eta == null) {
      return 1;
    }

    return eta.compareTo(other.eta);
  }
}
