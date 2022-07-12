package et.gov.ethgov.licensing.application.revocation;

import et.gov.ethgov.licensing.domain.revocation.Revocation;
import et.gov.ethgov.licensing.domain.revocation.RevocationRepository;
import java.util.List;
import java.util.Objects;

public final class ListRevocationUseCase {
    private final RevocationRepository repository;

    public ListRevocationUseCase(RevocationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Revocation> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
