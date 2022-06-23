package et.gov.ethgov.licensing.application.inspection;

import et.gov.ethgov.licensing.domain.inspection.Inspection;
import et.gov.ethgov.licensing.domain.inspection.InspectionRepository;
import java.util.List;
import java.util.Objects;

public final class ListInspectionUseCase {
    private final InspectionRepository repository;

    public ListInspectionUseCase(InspectionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Inspection> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
