package et.gov.ethgov.permits.application.permitinspection;

import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionRepository;
import java.util.List;
import java.util.Objects;

public final class ListPermitinspectionUseCase {
    private final PermitinspectionRepository repository;

    public ListPermitinspectionUseCase(PermitinspectionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Permitinspection> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
