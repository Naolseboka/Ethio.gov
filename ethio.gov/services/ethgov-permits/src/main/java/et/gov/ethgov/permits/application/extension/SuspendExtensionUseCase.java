package et.gov.ethgov.permits.application.extension;

import et.gov.ethgov.permits.domain.extension.Extension;
import et.gov.ethgov.permits.domain.extension.ExtensionId;
import et.gov.ethgov.permits.domain.extension.ExtensionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendExtensionUseCase {
    private final ExtensionRepository repository;
    private final Clock clock;

    public SuspendExtensionUseCase(ExtensionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExtensionId execute(SuspendExtensionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ExtensionId id = ExtensionId.parse(command.extensionId());
        Extension aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("extension not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendExtensionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String extensionId,
            String reason) {}
}
