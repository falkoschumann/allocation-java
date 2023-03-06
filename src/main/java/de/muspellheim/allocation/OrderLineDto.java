package de.muspellheim.allocation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "order_lines")
public class OrderLineDto {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private String sku;

  @Column(nullable = false)
  private int qty;

  @Column(name = "order_id")
  private String orderId;

  OrderLine toDomain() {
    return new OrderLine(orderId, sku, qty);
  }

  static OrderLineDto fromDomain(OrderLine line, EntityManager entityManager) {
    try {
      return entityManager.createQuery(
          """
             from OrderLineDto
            where orderId=:orderId and sku=:sku and qty=:qty
            """, OrderLineDto.class)
        .setParameter("orderId", line.orderId())
        .setParameter("sku", line.sku())
        .setParameter("qty", line.qty())
        .getSingleResult();
    } catch (NoResultException ignore) {
      var l = new OrderLineDto();
      l.setOrderId(line.orderId());
      l.setSku(line.sku());
      l.setQty(line.qty());
      return l;
    }
  }
}
