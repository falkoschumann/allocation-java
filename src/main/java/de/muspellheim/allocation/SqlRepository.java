package de.muspellheim.allocation;

import jakarta.persistence.EntityManager;

public class SqlRepository implements Repository {
  private final EntityManager entityManager;

  public SqlRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void add(Batch batch) {
    BatchDto.updateFromDomain(batch, entityManager);
  }

  @Override
  public Batch get(String reference) {
    return entityManager.createQuery(
        """
           from BatchDto
          where reference=:reference
          """, BatchDto.class)
      .setParameter("reference", reference)
      .getSingleResult()
      .toDomain();
  }
}
