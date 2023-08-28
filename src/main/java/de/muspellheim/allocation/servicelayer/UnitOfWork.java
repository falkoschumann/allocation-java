/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.Repository;
import de.muspellheim.allocation.util.ContextManager;
import lombok.Getter;

@Getter
public abstract class UnitOfWork extends ContextManager<UnitOfWork> {

  protected Repository products;

  public final void commit() {
    doCommit();
    publishEvents();
  }

  private void publishEvents() {
    for (var product : products.getSeen()) {
      while (!product.getEvents().isEmpty()) {
        var event = product.getEvents().poll();
        MessageBus.handle(event);
      }
    }
  }

  protected abstract void doCommit();

  public abstract void rollback();

  @Override
  protected UnitOfWork enter() {
    return this;
  }

  @Override
  protected boolean exit(Exception optionalException) {
    rollback();
    return false;
  }
}
