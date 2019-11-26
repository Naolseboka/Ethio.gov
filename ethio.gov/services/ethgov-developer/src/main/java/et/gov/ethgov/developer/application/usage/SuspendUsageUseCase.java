package et.gov.ethgov.developer.application.usage;

import et.gov.ethgov.developer.domain.usage.Usage;
import et.gov.ethgov.developer.domain.usage.UsageId;
import et.gov.ethgov.developer.domain.usage.UsageRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendUsageUseCase {
    private final UsageRepository repository;
    private final Clock clock;

    public SuspendUsageUseCase(UsageRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public UsageId execute(SuspendUsageCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        UsageId id = UsageId.parse(command.usageId());
        Usage aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("usage not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendUsageCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String usageId,
            String reason) {}
}
