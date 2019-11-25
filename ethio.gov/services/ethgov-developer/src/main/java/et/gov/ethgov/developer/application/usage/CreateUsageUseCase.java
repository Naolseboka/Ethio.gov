package et.gov.ethgov.developer.application.usage;

import et.gov.ethgov.developer.domain.usage.Usage;
import et.gov.ethgov.developer.domain.usage.UsageId;
import et.gov.ethgov.developer.domain.usage.UsageRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateUsageUseCase {
    private final UsageRepository repository;
    private final Clock clock;

    public CreateUsageUseCase(UsageRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public UsageId execute(CreateUsageCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Usage aggregate = Usage.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateUsageCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String usageId,
            String reason) {}
}
