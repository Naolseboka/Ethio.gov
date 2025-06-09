package et.gov.ethgov.search.application.query;

import et.gov.ethgov.search.domain.query.Query;
import et.gov.ethgov.search.domain.query.QueryRepository;
import java.util.List;
import java.util.Objects;

public final class ListQueryUseCase {
    private final QueryRepository repository;

    public ListQueryUseCase(QueryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Query> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
