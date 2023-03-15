package de.muspellheim.allocation;

import jakarta.persistence.EntityManager;
import java.util.Collection;

public class Services {
  public static String allocate(OrderLine line, Repository repository, EntityManager entityManager) {
    entityManager.getTransaction().begin();
    var batches = repository.list();
    if (!isValidSku(line.getSku(), batches)) {
      entityManager.getTransaction().rollback();
      throw new InvalidSku("Invalid sku %1$s".formatted(line.getSku()));
    }
    var batchRef = Allocations.allocate(line, batches);
    entityManager.getTransaction().commit();
    return batchRef;
  }

  private static boolean isValidSku(String sku, Collection<Batch> batches) {
    return batches.stream().anyMatch(b -> b.getSku().equals(sku));
  }
}
