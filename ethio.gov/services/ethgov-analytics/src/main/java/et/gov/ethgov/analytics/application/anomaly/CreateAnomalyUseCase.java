package et.gov.ethgov.analytics.application.anomaly;

import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyId;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAnomalyUseCase {
    private final AnomalyRepository repository;
    private final Clock clock;

    public CreateAnomalyUseCase(AnomalyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AnomalyId execute(CreateAnomalyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Anomaly aggregate = Anomaly.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAnomalyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String anomalyId,
            String reason) {}
}
