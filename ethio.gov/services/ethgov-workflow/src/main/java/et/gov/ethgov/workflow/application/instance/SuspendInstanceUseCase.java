package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.workflow.domain.instance.Instance;
import et.gov.ethgov.workflow.domain.instance.InstanceId;
import et.gov.ethgov.workflow.domain.instance.InstanceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendInstanceUseCase {
    private final InstanceRepository repository;
    private final Clock clock;

    public SuspendInstanceUseCase(InstanceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InstanceId execute(SuspendInstanceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InstanceId id = InstanceId.parse(command.instanceId());
        Instance aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("instance not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendInstanceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String instanceId,
            String reason) {}
}
