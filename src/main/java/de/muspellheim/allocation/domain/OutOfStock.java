/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.domain;

public class OutOfStock extends RuntimeException {

  OutOfStock(String message) {
    super(message);
  }
}
