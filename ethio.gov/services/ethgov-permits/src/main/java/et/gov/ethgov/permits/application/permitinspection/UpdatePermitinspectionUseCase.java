package et.gov.ethgov.permits.application.permitinspection;

import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionId;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePermitinspectionUseCase {
    private final PermitinspectionRepository repository;
    private final Clock clock;

    public UpdatePermitinspectionUseCase(PermitinspectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitinspectionId execute(UpdatePermitinspectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitinspectionId id = PermitinspectionId.parse(command.permitinspectionId());
        Permitinspection aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitinspection not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePermitinspectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitinspectionId,
            String reason) {}
}
