package et.gov.ethgov.developer.application.usage;

import et.gov.ethgov.developer.domain.usage.Usage;
import et.gov.ethgov.developer.domain.usage.UsageId;
import et.gov.ethgov.developer.domain.usage.UsageRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateUsageUseCase {
    private final UsageRepository repository;
    private final Clock clock;

    public ActivateUsageUseCase(UsageRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public UsageId execute(ActivateUsageCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        UsageId id = UsageId.parse(command.usageId());
        Usage aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("usage not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateUsageCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String usageId,
            String reason) {}
}
