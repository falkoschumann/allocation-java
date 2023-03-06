package de.muspellheim.allocation;

public interface Repository {
  void add(Batch batch);

  Batch get(String reference);
}
