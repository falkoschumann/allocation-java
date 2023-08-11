/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.adapters;

import de.muspellheim.allocation.domain.Product;
import java.util.Optional;

public interface Repository {
  void add(Product product);

  Optional<Product> get(String sku);
}
