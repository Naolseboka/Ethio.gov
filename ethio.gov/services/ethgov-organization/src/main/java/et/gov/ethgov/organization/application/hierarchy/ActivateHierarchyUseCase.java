package et.gov.ethgov.organization.application.hierarchy;

import et.gov.ethgov.organization.domain.hierarchy.Hierarchy;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyId;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateHierarchyUseCase {
    private final HierarchyRepository repository;
    private final Clock clock;

    public ActivateHierarchyUseCase(HierarchyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HierarchyId execute(ActivateHierarchyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HierarchyId id = HierarchyId.parse(command.hierarchyId());
        Hierarchy aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("hierarchy not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateHierarchyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String hierarchyId,
            String reason) {}
}
