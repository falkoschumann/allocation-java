/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.unit;

import de.muspellheim.allocation.adapters.Repository;
import de.muspellheim.allocation.domain.Product;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class FakeRepository implements Repository {
  private final Set<Product> products = new LinkedHashSet<>();

  FakeRepository(Product... products) {
    this.products.addAll(List.of(products));
  }

  @Override
  public void add(Product product) {
    products.add(product);
  }

  @Override
  public Optional<Product> get(String sku) {
    return products.stream().filter(p -> sku.equals(p.getSku())).findFirst();
  }
}
