package et.gov.ethgov.permits.application.permitinspection;

import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionId;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePermitinspectionUseCase {
    private final PermitinspectionRepository repository;
    private final Clock clock;

    public CreatePermitinspectionUseCase(PermitinspectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitinspectionId execute(CreatePermitinspectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Permitinspection aggregate = Permitinspection.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePermitinspectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitinspectionId,
            String reason) {}
}
