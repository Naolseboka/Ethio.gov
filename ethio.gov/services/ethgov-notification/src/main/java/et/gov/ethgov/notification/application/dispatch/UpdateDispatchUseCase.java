package et.gov.ethgov.notification.application.dispatch;

import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchId;
import et.gov.ethgov.notification.domain.dispatch.DispatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDispatchUseCase {
    private final DispatchRepository repository;
    private final Clock clock;

    public UpdateDispatchUseCase(DispatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DispatchId execute(UpdateDispatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DispatchId id = DispatchId.parse(command.dispatchId());
        Dispatch aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDispatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dispatchId,
            String reason) {}
}
