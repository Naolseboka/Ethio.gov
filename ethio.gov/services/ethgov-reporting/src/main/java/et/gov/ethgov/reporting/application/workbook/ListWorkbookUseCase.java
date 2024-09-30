package et.gov.ethgov.reporting.application.workbook;

import et.gov.ethgov.reporting.domain.workbook.Workbook;
import et.gov.ethgov.reporting.domain.workbook.WorkbookRepository;
import java.util.List;
import java.util.Objects;

public final class ListWorkbookUseCase {
    private final WorkbookRepository repository;

    public ListWorkbookUseCase(WorkbookRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Workbook> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
