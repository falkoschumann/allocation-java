/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.servicelayer;

public class InvalidSku extends RuntimeException {
  InvalidSku(String message) {
    super(message);
  }
}
