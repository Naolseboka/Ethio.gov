package et.gov.ethgov.payments.application.settlement;

import et.gov.ethgov.payments.domain.settlement.Settlement;
import et.gov.ethgov.payments.domain.settlement.SettlementId;
import et.gov.ethgov.payments.domain.settlement.SettlementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSettlementUseCase {
    private final SettlementRepository repository;
    private final Clock clock;

    public SuspendSettlementUseCase(SettlementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SettlementId execute(SuspendSettlementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SettlementId id = SettlementId.parse(command.settlementId());
        Settlement aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("settlement not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSettlementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String settlementId,
            String reason) {}
}
