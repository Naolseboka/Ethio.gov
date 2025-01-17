package et.gov.ethgov.revenue.application.collector;

import et.gov.ethgov.revenue.domain.collector.Collector;
import et.gov.ethgov.revenue.domain.collector.CollectorId;
import et.gov.ethgov.revenue.domain.collector.CollectorRepository;
import java.util.Objects;

public final class GetCollectorUseCase {
    private final CollectorRepository repository;

    public GetCollectorUseCase(CollectorRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Collector execute(String rawId) {
        CollectorId id = CollectorId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("collector not found: " + rawId));
    }
}
