package de.muspellheim.allocation.unit;

import de.muspellheim.allocation.servicelayer.UnitOfWork;

class FakeUnitOfWork extends UnitOfWork {
  private boolean committed;

  FakeUnitOfWork() {
    products = new FakeRepository();
  }

  boolean isCommitted() {
    return committed;
  }

  @Override
  public void commit() {
    committed = true;
  }

  @Override
  public void rollback() {}
}
