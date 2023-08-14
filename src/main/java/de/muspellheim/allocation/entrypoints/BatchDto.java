/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.entrypoints;

import java.time.LocalDate;

public record BatchDto(String ref, String sku, int qty, LocalDate eta) {}
