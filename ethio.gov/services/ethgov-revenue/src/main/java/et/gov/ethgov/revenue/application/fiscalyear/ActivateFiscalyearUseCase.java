package et.gov.ethgov.revenue.application.fiscalyear;

import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearId;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateFiscalyearUseCase {
    private final FiscalyearRepository repository;
    private final Clock clock;

    public ActivateFiscalyearUseCase(FiscalyearRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FiscalyearId execute(ActivateFiscalyearCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FiscalyearId id = FiscalyearId.parse(command.fiscalyearId());
        Fiscalyear aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("fiscalyear not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateFiscalyearCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String fiscalyearId,
            String reason) {}
}
