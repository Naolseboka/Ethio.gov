package et.gov.ethgov.workflow.application.escalation;

import et.gov.ethgov.workflow.domain.escalation.Escalation;
import et.gov.ethgov.workflow.domain.escalation.EscalationRepository;
import java.util.List;
import java.util.Objects;

public final class ListEscalationUseCase {
    private final EscalationRepository repository;

    public ListEscalationUseCase(EscalationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Escalation> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
