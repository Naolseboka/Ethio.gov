package et.gov.ethgov.permits.application.permitinspection;

import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionId;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPermitinspectionUseCase {
    private final PermitinspectionRepository repository;
    private final Clock clock;

    public SuspendPermitinspectionUseCase(PermitinspectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitinspectionId execute(SuspendPermitinspectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitinspectionId id = PermitinspectionId.parse(command.permitinspectionId());
        Permitinspection aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitinspection not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPermitinspectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitinspectionId,
            String reason) {}
}
