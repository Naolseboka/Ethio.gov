package et.gov.ethgov.analytics.application.funnel;

import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelId;
import et.gov.ethgov.analytics.domain.funnel.FunnelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateFunnelUseCase {
    private final FunnelRepository repository;
    private final Clock clock;

    public ActivateFunnelUseCase(FunnelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FunnelId execute(ActivateFunnelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FunnelId id = FunnelId.parse(command.funnelId());
        Funnel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("funnel not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateFunnelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String funnelId,
            String reason) {}
}
