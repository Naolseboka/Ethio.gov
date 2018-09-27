package et.gov.ethgov.analytics.application.funnel;

import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelId;
import et.gov.ethgov.analytics.domain.funnel.FunnelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateFunnelUseCase {
    private final FunnelRepository repository;
    private final Clock clock;

    public UpdateFunnelUseCase(FunnelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FunnelId execute(UpdateFunnelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FunnelId id = FunnelId.parse(command.funnelId());
        Funnel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("funnel not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateFunnelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String funnelId,
            String reason) {}
}
