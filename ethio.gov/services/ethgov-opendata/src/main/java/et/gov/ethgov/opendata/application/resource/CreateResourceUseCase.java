package et.gov.ethgov.opendata.application.resource;

import et.gov.ethgov.opendata.domain.resource.Resource;
import et.gov.ethgov.opendata.domain.resource.ResourceId;
import et.gov.ethgov.opendata.domain.resource.ResourceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateResourceUseCase {
    private final ResourceRepository repository;
    private final Clock clock;

    public CreateResourceUseCase(ResourceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ResourceId execute(CreateResourceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Resource aggregate = Resource.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateResourceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String resourceId,
            String reason) {}
}
