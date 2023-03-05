package de.muspellheim.allocation;

class OutOfStock extends RuntimeException {
  OutOfStock(String message) {
    super(message);
  }
}
