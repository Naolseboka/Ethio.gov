package et.gov.ethgov.organization.application.hierarchy;

import et.gov.ethgov.organization.domain.hierarchy.Hierarchy;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyId;
import et.gov.ethgov.organization.domain.hierarchy.HierarchyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateHierarchyUseCase {
    private final HierarchyRepository repository;
    private final Clock clock;

    public CreateHierarchyUseCase(HierarchyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HierarchyId execute(CreateHierarchyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Hierarchy aggregate = Hierarchy.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateHierarchyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String hierarchyId,
            String reason) {}
}
