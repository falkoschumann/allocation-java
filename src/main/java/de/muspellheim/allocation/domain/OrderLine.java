/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.domain;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(exclude = "id")
public class OrderLine {

  Long id;
  String orderId;
  String sku;
  int qty;

  public OrderLine(String orderId, String sku, int qty) {
    this(null, orderId, sku, qty);
  }

  private OrderLine(Long id, String orderId, String sku, int qty) {
    this.id = id;
    this.orderId = Objects.requireNonNull(orderId, "The orderId cannot be null.");
    this.sku = Objects.requireNonNull(sku, "The sku cannot be null.");
    this.qty = qty;
  }
}
