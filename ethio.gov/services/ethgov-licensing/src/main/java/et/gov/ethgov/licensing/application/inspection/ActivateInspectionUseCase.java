package et.gov.ethgov.licensing.application.inspection;

import et.gov.ethgov.licensing.domain.inspection.Inspection;
import et.gov.ethgov.licensing.domain.inspection.InspectionId;
import et.gov.ethgov.licensing.domain.inspection.InspectionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateInspectionUseCase {
    private final InspectionRepository repository;
    private final Clock clock;

    public ActivateInspectionUseCase(InspectionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InspectionId execute(ActivateInspectionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InspectionId id = InspectionId.parse(command.inspectionId());
        Inspection aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("inspection not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateInspectionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String inspectionId,
            String reason) {}
}
