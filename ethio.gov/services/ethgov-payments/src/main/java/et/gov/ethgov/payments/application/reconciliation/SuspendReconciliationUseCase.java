package et.gov.ethgov.payments.application.reconciliation;

import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationId;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendReconciliationUseCase {
    private final ReconciliationRepository repository;
    private final Clock clock;

    public SuspendReconciliationUseCase(ReconciliationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReconciliationId execute(SuspendReconciliationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReconciliationId id = ReconciliationId.parse(command.reconciliationId());
        Reconciliation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reconciliation not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendReconciliationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reconciliationId,
            String reason) {}
}
