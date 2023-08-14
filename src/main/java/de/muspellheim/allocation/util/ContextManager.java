/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.util;

import java.util.function.Consumer;

public abstract class ContextManager<T> {

  public void with(Runnable runnable) {
    enter();
    runnable.run();
    exit();
  }

  public void with(Consumer<T> consumer) {
    var context = enter();
    consumer.accept(context);
    exit();
  }

  protected abstract T enter();

  protected abstract void exit();
}
