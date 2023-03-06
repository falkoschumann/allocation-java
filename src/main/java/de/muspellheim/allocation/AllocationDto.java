package de.muspellheim.allocation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "allocations")
public class AllocationDto {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "orderline_id")
  private OrderLineDto orderLine;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "batch_id")
  private BatchDto batch;

  static AllocationDto fromDomain(OrderLine line, BatchDto batch, EntityManager entityManager) {
    OrderLineDto o = OrderLineDto.fromDomain(line, entityManager);
    try {
      return entityManager.createQuery(
          """
             from AllocationDto
            where orderLine=:orderLine and batch=:batch
            """, AllocationDto.class)
        .setParameter("orderLine", o)
        .setParameter("batch", batch)
        .getSingleResult();
    } catch (NoResultException ignore) {
      var a = new AllocationDto();
      a.setOrderLine(o);
      a.setBatch(batch);
      return a;
    }
  }
}
