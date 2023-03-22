package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.JpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class JpaUnitOfWork extends UnitOfWork {
  private final EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

  public JpaUnitOfWork(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @Override
  public void commit() {
    entityManager.getTransaction().commit();
  }

  @Override
  public void rollback() {
    entityManager.getTransaction().rollback();
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
    entityManager.close();
  }
}
