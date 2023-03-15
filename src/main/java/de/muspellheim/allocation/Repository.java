package de.muspellheim.allocation;

import java.util.List;

public interface Repository {
  void add(Batch batch);

  Batch get(String reference);

  List<Batch> list();
}
