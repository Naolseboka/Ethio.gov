package et.gov.ethgov.revenue.application.collector;

import et.gov.ethgov.revenue.domain.collector.Collector;
import et.gov.ethgov.revenue.domain.collector.CollectorRepository;
import java.util.List;
import java.util.Objects;

public final class ListCollectorUseCase {
    private final CollectorRepository repository;

    public ListCollectorUseCase(CollectorRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Collector> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
