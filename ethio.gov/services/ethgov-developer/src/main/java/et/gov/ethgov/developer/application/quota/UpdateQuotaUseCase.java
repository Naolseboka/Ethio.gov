package et.gov.ethgov.developer.application.quota;

import et.gov.ethgov.developer.domain.quota.Quota;
import et.gov.ethgov.developer.domain.quota.QuotaId;
import et.gov.ethgov.developer.domain.quota.QuotaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateQuotaUseCase {
    private final QuotaRepository repository;
    private final Clock clock;

    public UpdateQuotaUseCase(QuotaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QuotaId execute(UpdateQuotaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        QuotaId id = QuotaId.parse(command.quotaId());
        Quota aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("quota not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateQuotaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String quotaId,
            String reason) {}
}
