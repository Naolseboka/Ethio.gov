package et.gov.ethgov.reporting.application.metric;

import et.gov.ethgov.reporting.domain.metric.Metric;
import et.gov.ethgov.reporting.domain.metric.MetricRepository;
import java.util.List;
import java.util.Objects;

public final class ListMetricUseCase {
    private final MetricRepository repository;

    public ListMetricUseCase(MetricRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Metric> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
