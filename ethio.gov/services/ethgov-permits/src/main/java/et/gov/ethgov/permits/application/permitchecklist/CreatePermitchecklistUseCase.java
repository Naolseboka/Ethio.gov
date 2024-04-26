package et.gov.ethgov.permits.application.permitchecklist;

import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistId;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePermitchecklistUseCase {
    private final PermitchecklistRepository repository;
    private final Clock clock;

    public CreatePermitchecklistUseCase(PermitchecklistRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitchecklistId execute(CreatePermitchecklistCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Permitchecklist aggregate = Permitchecklist.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePermitchecklistCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitchecklistId,
            String reason) {}
}
