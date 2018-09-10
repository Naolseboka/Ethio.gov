package et.gov.ethgov.analytics.application.attribution;

import et.gov.ethgov.analytics.domain.attribution.Attribution;
import et.gov.ethgov.analytics.domain.attribution.AttributionId;
import et.gov.ethgov.analytics.domain.attribution.AttributionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAttributionUseCase {
    private final AttributionRepository repository;
    private final Clock clock;

    public UpdateAttributionUseCase(AttributionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AttributionId execute(UpdateAttributionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AttributionId id = AttributionId.parse(command.attributionId());
        Attribution aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("attribution not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAttributionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String attributionId,
            String reason) {}
}
