package et.gov.ethgov.reporting.domain.workbook;

import java.util.List;
import java.util.Optional;

public interface WorkbookRepository {
    Workbook save(Workbook aggregate);

    Optional<Workbook> findById(WorkbookId id);

    Optional<Workbook> findByReferenceCode(String tenantId, String referenceCode);

    List<Workbook> findByTenant(String tenantId, int limit);

    void delete(WorkbookId id);
}
