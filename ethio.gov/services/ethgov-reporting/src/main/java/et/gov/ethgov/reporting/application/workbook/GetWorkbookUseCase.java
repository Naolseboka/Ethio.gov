package et.gov.ethgov.reporting.application.workbook;

import et.gov.ethgov.reporting.domain.workbook.Workbook;
import et.gov.ethgov.reporting.domain.workbook.WorkbookId;
import et.gov.ethgov.reporting.domain.workbook.WorkbookRepository;
import java.util.Objects;

public final class GetWorkbookUseCase {
    private final WorkbookRepository repository;

    public GetWorkbookUseCase(WorkbookRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Workbook execute(String rawId) {
        WorkbookId id = WorkbookId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("workbook not found: " + rawId));
    }
}
