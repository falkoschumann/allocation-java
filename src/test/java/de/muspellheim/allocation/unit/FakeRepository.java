package de.muspellheim.allocation.unit;

import de.muspellheim.allocation.adapters.Repository;
import de.muspellheim.allocation.domain.Batch;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class FakeRepository implements Repository {
  private final Set<Batch> batches = new LinkedHashSet<>();

  FakeRepository(Batch... batches) {
    this.batches.addAll(List.of(batches));
  }

  @Override
  public void add(Batch batch) {
    batches.add(batch);
  }

  @Override
  public Batch get(String reference) {
    return batches.stream()
      .filter(b -> b.getReference().equals(reference))
      .findFirst()
      .orElseThrow();
  }

  @Override
  public List<Batch> list() {
    return List.copyOf(batches);
  }
}
