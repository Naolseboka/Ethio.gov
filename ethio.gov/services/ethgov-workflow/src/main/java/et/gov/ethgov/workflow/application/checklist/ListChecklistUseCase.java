package et.gov.ethgov.workflow.application.checklist;

import et.gov.ethgov.workflow.domain.checklist.Checklist;
import et.gov.ethgov.workflow.domain.checklist.ChecklistRepository;
import java.util.List;
import java.util.Objects;

public final class ListChecklistUseCase {
    private final ChecklistRepository repository;

    public ListChecklistUseCase(ChecklistRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Checklist> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
