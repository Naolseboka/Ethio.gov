package et.gov.ethgov.revenue.application.fiscalyear;

import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearId;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateFiscalyearUseCase {
    private final FiscalyearRepository repository;
    private final Clock clock;

    public CreateFiscalyearUseCase(FiscalyearRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FiscalyearId execute(CreateFiscalyearCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Fiscalyear aggregate = Fiscalyear.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateFiscalyearCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String fiscalyearId,
            String reason) {}
}
