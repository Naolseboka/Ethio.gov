package et.gov.ethgov.payments.application.reconciliation;

import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationId;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateReconciliationUseCase {
    private final ReconciliationRepository repository;
    private final Clock clock;

    public ActivateReconciliationUseCase(ReconciliationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReconciliationId execute(ActivateReconciliationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReconciliationId id = ReconciliationId.parse(command.reconciliationId());
        Reconciliation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reconciliation not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateReconciliationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reconciliationId,
            String reason) {}
}
