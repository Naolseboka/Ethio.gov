package et.gov.ethgov.opendata.application.resource;

import et.gov.ethgov.opendata.domain.resource.Resource;
import et.gov.ethgov.opendata.domain.resource.ResourceId;
import et.gov.ethgov.opendata.domain.resource.ResourceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateResourceUseCase {
    private final ResourceRepository repository;
    private final Clock clock;

    public ActivateResourceUseCase(ResourceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ResourceId execute(ActivateResourceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ResourceId id = ResourceId.parse(command.resourceId());
        Resource aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("resource not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateResourceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String resourceId,
            String reason) {}
}
