/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.adapters.Email;
import de.muspellheim.allocation.domain.Event;
import de.muspellheim.allocation.domain.OutOfStock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MessageBus {

  private static final Map<Class<? extends Event>, List<Consumer<Event>>> HANDLERS =
      new LinkedHashMap<>();

  static {
    HANDLERS.put(OutOfStock.class, List.of(e -> sendOutOfStockNotification((OutOfStock) e)));
  }

  private MessageBus() {}

  public static void handle(Event event) {
    for (var h : HANDLERS.get(event.getClass())) {
      h.accept(event);
    }
  }

  private static void sendOutOfStockNotification(OutOfStock event) {
    Email.sendMail("stock@made.com", "Out of stock for %s".formatted(event.sku()));
  }
}
