package et.gov.ethgov.payments.application.chargeback;

import et.gov.ethgov.payments.domain.chargeback.Chargeback;
import et.gov.ethgov.payments.domain.chargeback.ChargebackId;
import et.gov.ethgov.payments.domain.chargeback.ChargebackRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateChargebackUseCase {
    private final ChargebackRepository repository;
    private final Clock clock;

    public ActivateChargebackUseCase(ChargebackRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChargebackId execute(ActivateChargebackCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ChargebackId id = ChargebackId.parse(command.chargebackId());
        Chargeback aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("chargeback not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateChargebackCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String chargebackId,
            String reason) {}
}
