package et.gov.ethgov.payments.application.reconciliation;

import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationId;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateReconciliationUseCase {
    private final ReconciliationRepository repository;
    private final Clock clock;

    public CreateReconciliationUseCase(ReconciliationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReconciliationId execute(CreateReconciliationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Reconciliation aggregate = Reconciliation.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateReconciliationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reconciliationId,
            String reason) {}
}
