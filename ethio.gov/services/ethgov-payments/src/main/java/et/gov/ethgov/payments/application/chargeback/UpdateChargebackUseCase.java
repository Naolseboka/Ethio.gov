package et.gov.ethgov.payments.application.chargeback;

import et.gov.ethgov.payments.domain.chargeback.Chargeback;
import et.gov.ethgov.payments.domain.chargeback.ChargebackId;
import et.gov.ethgov.payments.domain.chargeback.ChargebackRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateChargebackUseCase {
    private final ChargebackRepository repository;
    private final Clock clock;

    public UpdateChargebackUseCase(ChargebackRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChargebackId execute(UpdateChargebackCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ChargebackId id = ChargebackId.parse(command.chargebackId());
        Chargeback aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("chargeback not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateChargebackCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String chargebackId,
            String reason) {}
}
