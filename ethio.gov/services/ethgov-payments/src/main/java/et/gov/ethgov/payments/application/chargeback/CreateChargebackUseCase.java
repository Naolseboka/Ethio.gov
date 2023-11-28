package et.gov.ethgov.payments.application.chargeback;

import et.gov.ethgov.payments.domain.chargeback.Chargeback;
import et.gov.ethgov.payments.domain.chargeback.ChargebackId;
import et.gov.ethgov.payments.domain.chargeback.ChargebackRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateChargebackUseCase {
    private final ChargebackRepository repository;
    private final Clock clock;

    public CreateChargebackUseCase(ChargebackRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChargebackId execute(CreateChargebackCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Chargeback aggregate = Chargeback.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateChargebackCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String chargebackId,
            String reason) {}
}
