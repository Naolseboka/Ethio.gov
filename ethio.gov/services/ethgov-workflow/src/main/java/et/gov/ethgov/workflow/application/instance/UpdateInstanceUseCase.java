package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.workflow.domain.instance.Instance;
import et.gov.ethgov.workflow.domain.instance.InstanceId;
import et.gov.ethgov.workflow.domain.instance.InstanceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateInstanceUseCase {
    private final InstanceRepository repository;
    private final Clock clock;

    public UpdateInstanceUseCase(InstanceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InstanceId execute(UpdateInstanceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InstanceId id = InstanceId.parse(command.instanceId());
        Instance aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("instance not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateInstanceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String instanceId,
            String reason) {}
}
