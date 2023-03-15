package de.muspellheim.allocation;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class OrderLine {
  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PRIVATE)
  private Long id;

  private final String orderId;

  private final String sku;

  private final int qty;

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof OrderLine l)) {
      return false;
    }

    return qty == l.qty && Objects.equals(orderId, l.orderId) && Objects.equals(sku, l.sku);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderId, sku, qty);
  }
}
