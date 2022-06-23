package et.gov.ethgov.licensing.application.inspection;

import et.gov.ethgov.licensing.domain.inspection.Inspection;
import et.gov.ethgov.licensing.domain.inspection.InspectionId;
import et.gov.ethgov.licensing.domain.inspection.InspectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendInspectionUseCase {
    private final InspectionRepository repository;
    private final Clock clock;

    public SuspendInspectionUseCase(InspectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InspectionId execute(SuspendInspectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InspectionId id = InspectionId.parse(command.inspectionId());
        Inspection aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("inspection not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendInspectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String inspectionId,
            String reason) {}
}
