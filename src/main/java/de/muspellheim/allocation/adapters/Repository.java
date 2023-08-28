/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.adapters;

import de.muspellheim.allocation.domain.Product;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;

@Getter
public abstract class Repository {

  private final Set<Product> seen = new LinkedHashSet<>();

  public final void add(Product product) {
    doAdd(product);
    seen.add(product);
  }

  public final Optional<Product> get(String sku) {
    var product = doGet(sku);
    product.ifPresent(seen::add);
    return product;
  }

  protected abstract void doAdd(Product product);

  protected abstract Optional<Product> doGet(String sku);
}
