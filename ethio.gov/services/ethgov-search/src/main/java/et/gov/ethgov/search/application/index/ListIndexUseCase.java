package et.gov.ethgov.search.application.index;

import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexRepository;
import java.util.List;
import java.util.Objects;

public final class ListIndexUseCase {
    private final IndexRepository repository;

    public ListIndexUseCase(IndexRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Index> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
