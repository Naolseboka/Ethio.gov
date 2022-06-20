package et.gov.ethgov.licensing.application.inspection;

import et.gov.ethgov.licensing.domain.inspection.Inspection;
import et.gov.ethgov.licensing.domain.inspection.InspectionId;
import et.gov.ethgov.licensing.domain.inspection.InspectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateInspectionUseCase {
    private final InspectionRepository repository;
    private final Clock clock;

    public CreateInspectionUseCase(InspectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InspectionId execute(CreateInspectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Inspection aggregate = Inspection.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateInspectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String inspectionId,
            String reason) {}
}
