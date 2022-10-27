package et.gov.ethgov.notification.application.dispatch;

import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchId;
import et.gov.ethgov.notification.domain.dispatch.DispatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDispatchUseCase {
    private final DispatchRepository repository;
    private final Clock clock;

    public ActivateDispatchUseCase(DispatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DispatchId execute(ActivateDispatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DispatchId id = DispatchId.parse(command.dispatchId());
        Dispatch aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDispatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dispatchId,
            String reason) {}
}
