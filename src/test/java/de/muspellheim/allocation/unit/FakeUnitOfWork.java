/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

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
  protected void doCommit() {
    committed = true;
  }

  @Override
  public void rollback() {}
}
