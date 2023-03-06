package de.muspellheim.allocation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "batches")
public class BatchDto {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private String reference;

  private String sku;

  @Column(name = "purchased_quantity", nullable = false)
  private int purchasedQuantity;

  @Nullable
  private LocalDate eta;

  @OneToMany
  private Set<OrderLineDto> allocations = new LinkedHashSet<>();

  static void updateFromDomain(Batch batch, EntityManager entityManager) {
    BatchDto b;
    try {
      b = entityManager.createQuery(
          """
             from BatchDto
            where reference=:reference
            """, BatchDto.class)
        .setParameter("reference", batch.getReference())
        .getSingleResult();
    } catch (NoResultException ignore) {
      b = new BatchDto();
      b.setReference(batch.getReference());
    }
    b.setSku(batch.getSku());
    b.setPurchasedQuantity(batch.getPurchasedQuantity());
    b.setEta(batch.getEta());
    entityManager.persist(b);
    b.allocations.clear();
    for (var l : batch.getAllocations()) {
      var a = AllocationDto.fromDomain(l, b, entityManager);
      b.allocations.add(a.getOrderLine());
    }
  }

  Batch toDomain() {
    var batch = new Batch(reference, sku, purchasedQuantity, eta);
    batch.getAllocations().addAll(allocations.stream().map(OrderLineDto::toDomain).toList());
    return batch;
  }
}
