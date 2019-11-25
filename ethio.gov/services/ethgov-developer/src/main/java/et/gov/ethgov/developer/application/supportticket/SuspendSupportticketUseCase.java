package et.gov.ethgov.developer.application.supportticket;

import et.gov.ethgov.developer.domain.supportticket.Supportticket;
import et.gov.ethgov.developer.domain.supportticket.SupportticketId;
import et.gov.ethgov.developer.domain.supportticket.SupportticketRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSupportticketUseCase {
    private final SupportticketRepository repository;
    private final Clock clock;

    public SuspendSupportticketUseCase(SupportticketRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SupportticketId execute(SuspendSupportticketCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SupportticketId id = SupportticketId.parse(command.supportticketId());
        Supportticket aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("supportticket not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSupportticketCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String supportticketId,
            String reason) {}
}
