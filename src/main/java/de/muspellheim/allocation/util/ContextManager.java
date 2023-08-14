/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.util;

public abstract class ContextManager<T> {

  public void with(Runnable runnable) {
    enter();
    try {
      runnable.run();
    } catch (Exception e) {
      if (exit(e)) {
        // exception was handled
        return;
      }

      throw e;
    }
    exit(null);
  }

  protected abstract T enter();

  protected abstract boolean exit(Exception optionalException);
}
