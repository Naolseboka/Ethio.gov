package et.gov.ethgov.notification.application.dispatch;

import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchId;
import et.gov.ethgov.notification.domain.dispatch.DispatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDispatchUseCase {
    private final DispatchRepository repository;
    private final Clock clock;

    public CreateDispatchUseCase(DispatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DispatchId execute(CreateDispatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Dispatch aggregate = Dispatch.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDispatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dispatchId,
            String reason) {}
}
