package et.gov.ethgov.search.application.boost;

import et.gov.ethgov.search.domain.boost.Boost;
import et.gov.ethgov.search.domain.boost.BoostRepository;
import java.util.List;
import java.util.Objects;

public final class ListBoostUseCase {
    private final BoostRepository repository;

    public ListBoostUseCase(BoostRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Boost> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
