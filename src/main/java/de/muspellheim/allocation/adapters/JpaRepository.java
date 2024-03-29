/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.adapters;

import de.muspellheim.allocation.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Objects;
import java.util.Optional;

public class JpaRepository extends Repository {

  private final EntityManager entityManager;

  public JpaRepository(EntityManager entityManager) {
    this.entityManager = Objects.requireNonNull(entityManager, "The entityManager cannot be null.");
  }

  @Override
  protected void doAdd(Product product) {
    entityManager.persist(product);
  }

  @Override
  protected Optional<Product> doGet(String sku) {
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
