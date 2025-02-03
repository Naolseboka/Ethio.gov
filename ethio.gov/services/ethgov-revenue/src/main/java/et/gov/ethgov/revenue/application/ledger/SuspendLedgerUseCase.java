package et.gov.ethgov.revenue.application.ledger;

import et.gov.ethgov.revenue.domain.ledger.Ledger;
import et.gov.ethgov.revenue.domain.ledger.LedgerId;
import et.gov.ethgov.revenue.domain.ledger.LedgerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendLedgerUseCase {
    private final LedgerRepository repository;
    private final Clock clock;

    public SuspendLedgerUseCase(LedgerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LedgerId execute(SuspendLedgerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        LedgerId id = LedgerId.parse(command.ledgerId());
        Ledger aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ledger not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendLedgerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String ledgerId,
            String reason) {}
}
