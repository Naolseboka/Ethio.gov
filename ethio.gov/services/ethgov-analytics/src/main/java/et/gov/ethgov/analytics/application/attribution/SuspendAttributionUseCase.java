package et.gov.ethgov.analytics.application.attribution;

import et.gov.ethgov.analytics.domain.attribution.Attribution;
import et.gov.ethgov.analytics.domain.attribution.AttributionId;
import et.gov.ethgov.analytics.domain.attribution.AttributionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendAttributionUseCase {
    private final AttributionRepository repository;
    private final Clock clock;

    public SuspendAttributionUseCase(AttributionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AttributionId execute(SuspendAttributionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AttributionId id = AttributionId.parse(command.attributionId());
        Attribution aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("attribution not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendAttributionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String attributionId,
            String reason) {}
}
