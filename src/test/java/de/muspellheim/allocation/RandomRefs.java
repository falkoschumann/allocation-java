/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation;

import java.util.UUID;

public class RandomRefs {
  private RandomRefs() {}

  public static String randomSku() {
    return randomSku("");
  }

  public static String randomSku(String name) {
    return "sku-%s-%s".formatted(name, randomSuffix());
  }

  public static String randomBatchRef() {
    return randomBatchRef("");
  }

  public static String randomBatchRef(String name) {
    return "batch-%s-%s".formatted(name, randomSuffix());
  }

  public static String randomOrderId() {
    return randomOrderId("");
  }

  public static String randomOrderId(String name) {
    return "order-%s-%s".formatted(name, randomSuffix());
  }

  private static String randomSuffix() {
    return UUID.randomUUID().toString().substring(0, 6);
  }
}
