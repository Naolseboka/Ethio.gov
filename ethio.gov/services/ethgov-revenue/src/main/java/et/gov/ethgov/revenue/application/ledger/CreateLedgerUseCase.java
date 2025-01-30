package et.gov.ethgov.revenue.application.ledger;

import et.gov.ethgov.revenue.domain.ledger.Ledger;
import et.gov.ethgov.revenue.domain.ledger.LedgerId;
import et.gov.ethgov.revenue.domain.ledger.LedgerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateLedgerUseCase {
    private final LedgerRepository repository;
    private final Clock clock;

    public CreateLedgerUseCase(LedgerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LedgerId execute(CreateLedgerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Ledger aggregate = Ledger.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateLedgerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String ledgerId,
            String reason) {}
}
