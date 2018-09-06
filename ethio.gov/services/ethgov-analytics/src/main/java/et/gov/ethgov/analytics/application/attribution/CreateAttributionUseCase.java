package et.gov.ethgov.analytics.application.attribution;

import et.gov.ethgov.analytics.domain.attribution.Attribution;
import et.gov.ethgov.analytics.domain.attribution.AttributionId;
import et.gov.ethgov.analytics.domain.attribution.AttributionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAttributionUseCase {
    private final AttributionRepository repository;
    private final Clock clock;

    public CreateAttributionUseCase(AttributionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AttributionId execute(CreateAttributionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Attribution aggregate = Attribution.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAttributionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String attributionId,
            String reason) {}
}
