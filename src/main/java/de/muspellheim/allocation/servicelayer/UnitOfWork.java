/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.Repository;
import de.muspellheim.allocation.util.ContextManager;
import lombok.Getter;

public abstract class UnitOfWork extends ContextManager<UnitOfWork> {

  @Getter protected Repository products;

  public abstract void commit();

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
