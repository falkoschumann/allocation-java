package de.muspellheim.allocation.adapters;

import de.muspellheim.allocation.domain.Batch;
import java.util.List;

public interface Repository {
  void add(Batch batch);

  Batch get(String reference);

  List<Batch> list();
}
