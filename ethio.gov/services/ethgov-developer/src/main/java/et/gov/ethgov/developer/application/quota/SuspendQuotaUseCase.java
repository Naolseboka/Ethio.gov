package et.gov.ethgov.developer.application.quota;

import et.gov.ethgov.developer.domain.quota.Quota;
import et.gov.ethgov.developer.domain.quota.QuotaId;
import et.gov.ethgov.developer.domain.quota.QuotaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendQuotaUseCase {
    private final QuotaRepository repository;
    private final Clock clock;

    public SuspendQuotaUseCase(QuotaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QuotaId execute(SuspendQuotaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        QuotaId id = QuotaId.parse(command.quotaId());
        Quota aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("quota not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendQuotaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String quotaId,
            String reason) {}
}
