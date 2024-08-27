package et.gov.ethgov.reporting.application.metric;

import et.gov.ethgov.reporting.domain.metric.Metric;
import et.gov.ethgov.reporting.domain.metric.MetricId;
import et.gov.ethgov.reporting.domain.metric.MetricRepository;
import java.util.Objects;

public final class GetMetricUseCase {
    private final MetricRepository repository;

    public GetMetricUseCase(MetricRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Metric execute(String rawId) {
        MetricId id = MetricId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("metric not found: " + rawId));
    }
}
