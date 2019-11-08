package et.gov.ethgov.developer.application.devwebhook;

import et.gov.ethgov.developer.domain.devwebhook.Devwebhook;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookId;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDevwebhookUseCase {
    private final DevwebhookRepository repository;
    private final Clock clock;

    public UpdateDevwebhookUseCase(DevwebhookRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevwebhookId execute(UpdateDevwebhookCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DevwebhookId id = DevwebhookId.parse(command.devwebhookId());
        Devwebhook aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devwebhook not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDevwebhookCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devwebhookId,
            String reason) {}
}
