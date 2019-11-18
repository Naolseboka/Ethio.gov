package et.gov.ethgov.developer.application.quota;

import et.gov.ethgov.developer.domain.quota.Quota;
import et.gov.ethgov.developer.domain.quota.QuotaId;
import et.gov.ethgov.developer.domain.quota.QuotaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateQuotaUseCase {
    private final QuotaRepository repository;
    private final Clock clock;

    public CreateQuotaUseCase(QuotaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QuotaId execute(CreateQuotaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Quota aggregate = Quota.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateQuotaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String quotaId,
            String reason) {}
}
