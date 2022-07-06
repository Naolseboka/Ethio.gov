package et.gov.ethgov.licensing.application.licensefee;

import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeRepository;
import java.util.List;
import java.util.Objects;

public final class ListLicensefeeUseCase {
    private final LicensefeeRepository repository;

    public ListLicensefeeUseCase(LicensefeeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Licensefee> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
