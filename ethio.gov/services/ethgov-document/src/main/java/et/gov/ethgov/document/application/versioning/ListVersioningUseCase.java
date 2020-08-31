package et.gov.ethgov.document.application.versioning;

import et.gov.ethgov.document.domain.versioning.Versioning;
import et.gov.ethgov.document.domain.versioning.VersioningRepository;
import java.util.List;
import java.util.Objects;

public final class ListVersioningUseCase {
    private final VersioningRepository repository;

    public ListVersioningUseCase(VersioningRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Versioning> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
