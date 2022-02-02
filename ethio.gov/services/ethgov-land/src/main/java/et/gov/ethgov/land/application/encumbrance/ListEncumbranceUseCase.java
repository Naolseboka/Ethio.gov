package et.gov.ethgov.land.application.encumbrance;

import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceRepository;
import java.util.List;
import java.util.Objects;

public final class ListEncumbranceUseCase {
    private final EncumbranceRepository repository;

    public ListEncumbranceUseCase(EncumbranceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Encumbrance> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
