package et.gov.ethgov.payments.application.settlement;

import et.gov.ethgov.payments.domain.settlement.Settlement;
import et.gov.ethgov.payments.domain.settlement.SettlementId;
import et.gov.ethgov.payments.domain.settlement.SettlementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSettlementUseCase {
    private final SettlementRepository repository;
    private final Clock clock;

    public CreateSettlementUseCase(SettlementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SettlementId execute(CreateSettlementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Settlement aggregate = Settlement.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSettlementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String settlementId,
            String reason) {}
}
