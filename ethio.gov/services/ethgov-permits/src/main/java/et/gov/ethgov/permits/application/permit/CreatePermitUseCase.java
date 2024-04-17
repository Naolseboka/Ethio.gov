package et.gov.ethgov.permits.application.permit;

import et.gov.ethgov.permits.domain.permit.Permit;
import et.gov.ethgov.permits.domain.permit.PermitId;
import et.gov.ethgov.permits.domain.permit.PermitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePermitUseCase {
    private final PermitRepository repository;
    private final Clock clock;

    public CreatePermitUseCase(PermitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitId execute(CreatePermitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Permit aggregate = Permit.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePermitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitId,
            String reason) {}
}
