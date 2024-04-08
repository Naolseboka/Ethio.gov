package et.gov.ethgov.permits.application.extension;

import et.gov.ethgov.permits.domain.extension.Extension;
import et.gov.ethgov.permits.domain.extension.ExtensionId;
import et.gov.ethgov.permits.domain.extension.ExtensionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateExtensionUseCase {
    private final ExtensionRepository repository;
    private final Clock clock;

    public CreateExtensionUseCase(ExtensionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExtensionId execute(CreateExtensionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Extension aggregate = Extension.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateExtensionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String extensionId,
            String reason) {}
}
