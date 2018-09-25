package et.gov.ethgov.analytics.application.funnel;

import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelId;
import et.gov.ethgov.analytics.domain.funnel.FunnelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateFunnelUseCase {
    private final FunnelRepository repository;
    private final Clock clock;

    public CreateFunnelUseCase(FunnelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FunnelId execute(CreateFunnelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Funnel aggregate = Funnel.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateFunnelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String funnelId,
            String reason) {}
}
