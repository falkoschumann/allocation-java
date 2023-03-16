package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.Repository;
import de.muspellheim.allocation.util.ContextManager;
import lombok.Getter;

public abstract class UnitOfWork implements ContextManager<UnitOfWork> {
  @Getter protected Repository batches;

  public abstract void commit();

  public abstract void rollback();

  @Override
  public UnitOfWork enter() {
    return this;
  }

  @Override
  public void exit() {
    rollback();
  }
}
