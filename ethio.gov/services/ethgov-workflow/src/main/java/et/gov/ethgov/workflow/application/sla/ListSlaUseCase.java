package et.gov.ethgov.workflow.application.sla;

import et.gov.ethgov.workflow.domain.sla.Sla;
import et.gov.ethgov.workflow.domain.sla.SlaRepository;
import java.util.List;
import java.util.Objects;

public final class ListSlaUseCase {
    private final SlaRepository repository;

    public ListSlaUseCase(SlaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Sla> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
