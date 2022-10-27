package et.gov.ethgov.notification.application.dispatch;

import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchId;
import et.gov.ethgov.notification.domain.dispatch.DispatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDispatchUseCase {
    private final DispatchRepository repository;
    private final Clock clock;

    public SuspendDispatchUseCase(DispatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DispatchId execute(SuspendDispatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DispatchId id = DispatchId.parse(command.dispatchId());
        Dispatch aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDispatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dispatchId,
            String reason) {}
}
