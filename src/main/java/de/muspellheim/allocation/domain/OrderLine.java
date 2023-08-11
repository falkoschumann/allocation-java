/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(exclude = "id")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class OrderLine {
  Long id;
  String orderId;
  String sku;
  int qty;

  public OrderLine(String orderId, String sku, int qty) {
    this(null, orderId, sku, qty);
  }
}
