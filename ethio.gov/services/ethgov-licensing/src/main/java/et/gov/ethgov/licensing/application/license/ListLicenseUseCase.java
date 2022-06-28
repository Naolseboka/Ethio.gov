package et.gov.ethgov.licensing.application.license;

import et.gov.ethgov.licensing.domain.license.License;
import et.gov.ethgov.licensing.domain.license.LicenseRepository;
import java.util.List;
import java.util.Objects;

public final class ListLicenseUseCase {
    private final LicenseRepository repository;

    public ListLicenseUseCase(LicenseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<License> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
