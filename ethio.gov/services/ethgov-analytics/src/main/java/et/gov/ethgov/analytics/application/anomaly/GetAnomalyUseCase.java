package et.gov.ethgov.analytics.application.anomaly;

import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyId;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyRepository;
import java.util.Objects;

public final class GetAnomalyUseCase {
    private final AnomalyRepository repository;

    public GetAnomalyUseCase(AnomalyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Anomaly execute(String rawId) {
        AnomalyId id = AnomalyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("anomaly not found: " + rawId));
    }
}
