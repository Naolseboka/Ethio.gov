package et.gov.ethgov.analytics.application.anomaly;

import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyId;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAnomalyUseCase {
    private final AnomalyRepository repository;
    private final Clock clock;

    public UpdateAnomalyUseCase(AnomalyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AnomalyId execute(UpdateAnomalyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AnomalyId id = AnomalyId.parse(command.anomalyId());
        Anomaly aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("anomaly not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAnomalyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String anomalyId,
            String reason) {}
}
