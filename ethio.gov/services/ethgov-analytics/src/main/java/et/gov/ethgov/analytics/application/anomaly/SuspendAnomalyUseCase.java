package et.gov.ethgov.analytics.application.anomaly;

import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyId;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendAnomalyUseCase {
    private final AnomalyRepository repository;
    private final Clock clock;

    public SuspendAnomalyUseCase(AnomalyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AnomalyId execute(SuspendAnomalyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AnomalyId id = AnomalyId.parse(command.anomalyId());
        Anomaly aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("anomaly not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendAnomalyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String anomalyId,
            String reason) {}
}
