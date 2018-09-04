package et.gov.ethgov.analytics.application.anomaly;

import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyRepository;
import java.util.List;
import java.util.Objects;

public final class ListAnomalyUseCase {
    private final AnomalyRepository repository;

    public ListAnomalyUseCase(AnomalyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Anomaly> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
