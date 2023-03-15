package de.muspellheim.allocation;

import jakarta.persistence.EntityManager;
import java.util.List;

public class JpaRepository implements Repository {
  private final EntityManager entityManager;

  public JpaRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void add(Batch batch) {
    entityManager.persist(batch);
  }

  @Override
  public Batch get(String reference) {
    return entityManager
        .createQuery("from Batch where reference=:reference", Batch.class)
        .setParameter("reference", reference)
        .getSingleResult();
  }

  @Override
  public List<Batch> list() {
    return entityManager.createQuery("from Batch", Batch.class).getResultList();
  }
}
