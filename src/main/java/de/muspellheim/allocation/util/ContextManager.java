/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.util;

import java.util.function.Consumer;

public abstract class ContextManager<T> {

  public void with(Runnable runnable) {
    with(c -> runnable.run());
  }

  public void with(Consumer<T> consumer) {
    var context = enter();
    try {
      consumer.accept(context);
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
