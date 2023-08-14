/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.JpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Objects;

public class JpaUnitOfWork extends UnitOfWork {

  private final EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  public JpaUnitOfWork(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory =
        Objects.requireNonNull(entityManagerFactory, "The entityManagerFactory cannot be null.");
  }

  public EntityManager getEntityManager() {
    return Objects.requireNonNull(entityManager);
  }

  @Override
  public void commit() {
    Objects.requireNonNull(entityManager).getTransaction().commit();
  }

  @Override
  public void rollback() {
    Objects.requireNonNull(entityManager).getTransaction().rollback();
  }

  @Override
  protected UnitOfWork enter() {
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    products = new JpaRepository(entityManager);
    return super.enter();
  }

  @Override
  protected boolean exit(Exception optionalException) {
    var handled = super.exit(optionalException);
    entityManager.close();
    return handled;
  }
}
