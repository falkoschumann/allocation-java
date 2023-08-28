/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.JpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Objects;
import lombok.Getter;

public class JpaUnitOfWork extends UnitOfWork {

  private final EntityManagerFactory entityManagerFactory;

  @Getter private EntityManager entityManager;

  public JpaUnitOfWork(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory =
        Objects.requireNonNull(entityManagerFactory, "The entityManagerFactory cannot be null.");
  }

  @Override
  protected void doCommit() {
    entityManager.getTransaction().commit();
  }

  @Override
  public void rollback() {
    entityManager.getTransaction().rollback();
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
