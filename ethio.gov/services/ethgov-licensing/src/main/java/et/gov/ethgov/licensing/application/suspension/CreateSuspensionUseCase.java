package et.gov.ethgov.licensing.application.suspension;

import et.gov.ethgov.licensing.domain.suspension.Suspension;
import et.gov.ethgov.licensing.domain.suspension.SuspensionId;
import et.gov.ethgov.licensing.domain.suspension.SuspensionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSuspensionUseCase {
    private final SuspensionRepository repository;
    private final Clock clock;

    public CreateSuspensionUseCase(SuspensionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuspensionId execute(CreateSuspensionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Suspension aggregate = Suspension.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSuspensionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String suspensionId,
            String reason) {}
}
