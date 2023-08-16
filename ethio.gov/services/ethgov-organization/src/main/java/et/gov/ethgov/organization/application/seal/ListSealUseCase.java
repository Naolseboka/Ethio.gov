package et.gov.ethgov.organization.application.seal;

import et.gov.ethgov.organization.domain.seal.Seal;
import et.gov.ethgov.organization.domain.seal.SealRepository;
import java.util.List;
import java.util.Objects;

public final class ListSealUseCase {
    private final SealRepository repository;

    public ListSealUseCase(SealRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Seal> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
