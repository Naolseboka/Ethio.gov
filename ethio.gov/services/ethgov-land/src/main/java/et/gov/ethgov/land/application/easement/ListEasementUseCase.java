package et.gov.ethgov.land.application.easement;

import et.gov.ethgov.land.domain.easement.Easement;
import et.gov.ethgov.land.domain.easement.EasementRepository;
import java.util.List;
import java.util.Objects;

public final class ListEasementUseCase {
    private final EasementRepository repository;

    public ListEasementUseCase(EasementRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Easement> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
