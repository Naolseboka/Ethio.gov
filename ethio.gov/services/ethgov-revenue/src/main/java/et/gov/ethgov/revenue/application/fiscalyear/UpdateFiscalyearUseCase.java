package et.gov.ethgov.revenue.application.fiscalyear;

import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearId;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateFiscalyearUseCase {
    private final FiscalyearRepository repository;
    private final Clock clock;

    public UpdateFiscalyearUseCase(FiscalyearRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FiscalyearId execute(UpdateFiscalyearCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FiscalyearId id = FiscalyearId.parse(command.fiscalyearId());
        Fiscalyear aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("fiscalyear not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateFiscalyearCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String fiscalyearId,
            String reason) {}
}
