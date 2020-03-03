package et.gov.ethgov.directory.application.counter;

import et.gov.ethgov.directory.domain.counter.Counter;
import et.gov.ethgov.directory.domain.counter.CounterRepository;
import java.util.List;
import java.util.Objects;

public final class ListCounterUseCase {
    private final CounterRepository repository;

    public ListCounterUseCase(CounterRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Counter> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
