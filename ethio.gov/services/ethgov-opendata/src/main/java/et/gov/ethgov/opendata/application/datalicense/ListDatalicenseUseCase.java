package et.gov.ethgov.opendata.application.datalicense;

import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseRepository;
import java.util.List;
import java.util.Objects;

public final class ListDatalicenseUseCase {
    private final DatalicenseRepository repository;

    public ListDatalicenseUseCase(DatalicenseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Datalicense> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
