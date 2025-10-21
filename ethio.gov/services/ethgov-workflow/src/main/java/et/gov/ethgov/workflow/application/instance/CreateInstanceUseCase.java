package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.workflow.domain.instance.Instance;
import et.gov.ethgov.workflow.domain.instance.InstanceId;
import et.gov.ethgov.workflow.domain.instance.InstanceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateInstanceUseCase {
    private final InstanceRepository repository;
    private final Clock clock;

    public CreateInstanceUseCase(InstanceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InstanceId execute(CreateInstanceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Instance aggregate = Instance.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateInstanceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String instanceId,
            String reason) {}
}
