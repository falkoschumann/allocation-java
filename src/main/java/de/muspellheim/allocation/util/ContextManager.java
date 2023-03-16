package de.muspellheim.allocation.util;

import java.util.function.Consumer;

public interface ContextManager<T> {
  default void with(Runnable runnable) {
    enter();
    runnable.run();
    exit();
  }

  default void with(Consumer<T> consumer) {
    var context = enter();
    consumer.accept(context);
    exit();
  }

  T enter();

  void exit();
}
