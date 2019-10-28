package et.gov.ethgov.developer.application.devapplication;

import et.gov.ethgov.developer.domain.devapplication.Devapplication;
import et.gov.ethgov.developer.domain.devapplication.DevapplicationId;
import et.gov.ethgov.developer.domain.devapplication.DevapplicationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDevapplicationUseCase {
    private final DevapplicationRepository repository;
    private final Clock clock;

    public UpdateDevapplicationUseCase(DevapplicationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevapplicationId execute(UpdateDevapplicationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DevapplicationId id = DevapplicationId.parse(command.devapplicationId());
        Devapplication aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devapplication not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDevapplicationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devapplicationId,
            String reason) {}
}
