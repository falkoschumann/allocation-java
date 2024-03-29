/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.entrypoints;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageResponse {

  private String message;
}
