package de.muspellheim.allocation.domain;

public class OutOfStock extends RuntimeException {
  OutOfStock(String message) {
    super(message);
  }
}
