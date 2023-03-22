package de.muspellheim.allocation.servicelayer;

import de.muspellheim.allocation.domain.Batch;
import de.muspellheim.allocation.domain.OrderLine;
import de.muspellheim.allocation.domain.Product;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.lang.Nullable;

public class Services {
  public static void addBatch(
      String ref, String sku, int qty, @Nullable LocalDate eta, UnitOfWork uow) {
    uow.with(
        () -> {
          var product =
              uow.getProducts()
                  .get(sku)
                  .orElseGet(
                      () -> {
                        var p = new Product(sku);
                        uow.getProducts().add(p);
                        return p;
                      });
          product.getBatches().add(new Batch(ref, sku, qty, eta));
          uow.commit();
        });
  }

  public static String allocate(String orderId, String sku, int qty, UnitOfWork uow) {
    var line = new OrderLine(orderId, sku, qty);
    var batchRef = new AtomicReference<String>();
    uow.with(
        () -> {
          var product =
              uow.getProducts()
                  .get(line.getSku())
                  .orElseThrow(() -> new InvalidSku("Invalid sku %1$s".formatted(line.getSku())));
          batchRef.set(product.allocate(line));
          uow.commit();
        });
    return batchRef.get();
  }
}
