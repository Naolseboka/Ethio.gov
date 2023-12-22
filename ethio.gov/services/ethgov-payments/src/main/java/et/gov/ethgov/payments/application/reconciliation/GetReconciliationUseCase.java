package et.gov.ethgov.payments.application.reconciliation;

import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationId;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationRepository;
import java.util.Objects;

public final class GetReconciliationUseCase {
    private final ReconciliationRepository repository;

    public GetReconciliationUseCase(ReconciliationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Reconciliation execute(String rawId) {
        ReconciliationId id = ReconciliationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reconciliation not found: " + rawId));
    }
}
