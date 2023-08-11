/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.JpaRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Objects;

public class JpaUnitOfWork extends UnitOfWork {
  private final EntityManagerFactory entityManagerFactory;
  @Nullable private EntityManager entityManager;

  public JpaUnitOfWork(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
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
  public UnitOfWork enter() {
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    products = new JpaRepository(entityManager);
    return super.enter();
  }

  @Override
  public void exit() {
    super.exit();
    Objects.requireNonNull(entityManager).close();
  }
}
