package et.gov.ethgov.analytics.application.funnel;

import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelId;
import et.gov.ethgov.analytics.domain.funnel.FunnelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendFunnelUseCase {
    private final FunnelRepository repository;
    private final Clock clock;

    public SuspendFunnelUseCase(FunnelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FunnelId execute(SuspendFunnelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FunnelId id = FunnelId.parse(command.funnelId());
        Funnel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("funnel not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendFunnelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String funnelId,
            String reason) {}
}
