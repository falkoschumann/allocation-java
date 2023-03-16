package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.domain.Allocations;
import de.muspellheim.allocation.domain.Batch;
import de.muspellheim.allocation.domain.OrderLine;
import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.lang.Nullable;

public class Services {
  public static void addBatch(
      String ref, String sku, int qty, @Nullable LocalDate eta, UnitOfWork uow) {
    uow.with(
        () -> {
          uow.getBatches().add(new Batch(ref, sku, qty, eta));
          uow.commit();
        });
  }

  public static String allocate(String orderId, String sku, int qty, UnitOfWork uow) {
    var line = new OrderLine(orderId, sku, qty);
    var batchRef = new AtomicReference<String>();
    uow.with(
        () -> {
          var batches = uow.getBatches().list();
          if (!isValidSku(line.getSku(), batches)) {
            throw new InvalidSku("Invalid sku %1$s".formatted(line.getSku()));
          }
          batchRef.set(Allocations.allocate(line, batches));
          uow.commit();
        });
    return batchRef.get();
  }

  private static boolean isValidSku(String sku, Collection<Batch> batches) {
    return batches.stream().anyMatch(b -> b.getSku().equals(sku));
  }
}
