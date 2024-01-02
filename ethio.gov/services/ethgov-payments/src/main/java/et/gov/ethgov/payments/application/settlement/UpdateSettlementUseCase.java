package et.gov.ethgov.payments.application.settlement;

import et.gov.ethgov.payments.domain.settlement.Settlement;
import et.gov.ethgov.payments.domain.settlement.SettlementId;
import et.gov.ethgov.payments.domain.settlement.SettlementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSettlementUseCase {
    private final SettlementRepository repository;
    private final Clock clock;

    public UpdateSettlementUseCase(SettlementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SettlementId execute(UpdateSettlementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SettlementId id = SettlementId.parse(command.settlementId());
        Settlement aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("settlement not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSettlementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String settlementId,
            String reason) {}
}
