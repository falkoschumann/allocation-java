/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.adapters;

import de.muspellheim.allocation.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Optional;

public class JpaRepository implements Repository {
  private final EntityManager entityManager;

  public JpaRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void add(Product product) {
    entityManager.persist(product);
  }

  @Override
  public Optional<Product> get(String sku) {
    try {
      var product =
          entityManager
              .createQuery("from Product where sku=:sku", Product.class)
              .setParameter("sku", sku)
              .getSingleResult();
      return Optional.of(product);
    } catch (NoResultException ignore) {
      return Optional.empty();
    }
  }
}
