package de.muspellheim.allocation;

import java.time.LocalDate;
import org.springframework.lang.Nullable;

public record StockItem(String ref, String sku, int qty, @Nullable LocalDate eta) {
}
