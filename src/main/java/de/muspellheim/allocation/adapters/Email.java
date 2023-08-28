/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.adapters;

public class Email {

  public static void sendMail(String to, String body) {
    System.out.printf("SENDING EMAIL: %s, %s\n", to, body);
  }
}
