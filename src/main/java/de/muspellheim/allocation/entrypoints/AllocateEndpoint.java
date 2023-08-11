/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.entrypoints;

import de.muspellheim.allocation.domain.OrderLine;
import de.muspellheim.allocation.domain.OutOfStock;
import de.muspellheim.allocation.servicelayer.InvalidSku;
import de.muspellheim.allocation.servicelayer.JpaUnitOfWork;
import de.muspellheim.allocation.servicelayer.Services;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AllocateEndpoint {
  private final EntityManagerFactory entityManagerFactory;

  public AllocateEndpoint(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @PostMapping(path = "/add-batch", consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public void addBatch(@RequestBody BatchDto batch) {
    var uow = new JpaUnitOfWork(entityManagerFactory);
    Services.addBatch(batch.ref(), batch.sku(), batch.qty(), batch.eta(), uow);
  }

  @PostMapping(path = "/allocate", consumes = "application/json", produces = "application/json")
  public Object allocate(@RequestBody OrderLine line, HttpServletResponse response) {
    try {
      var uow = new JpaUnitOfWork(entityManagerFactory);
      var batchref = Services.allocate(line.getOrderId(), line.getSku(), line.getQty(), uow);
      response.setStatus(HttpServletResponse.SC_CREATED);
      return new AllocateResponse(batchref);
    } catch (OutOfStock | InvalidSku e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return new ErrorMessageResponse(e.getMessage());
    }
  }
}
