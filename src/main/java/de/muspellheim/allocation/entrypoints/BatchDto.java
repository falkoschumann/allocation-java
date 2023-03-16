package de.muspellheim.allocation.entrypoints;

import java.time.LocalDate;
import org.springframework.lang.Nullable;

public record BatchDto(String ref, String sku, int qty, @Nullable LocalDate eta) {}
