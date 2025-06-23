package et.gov.ethgov.search.application.stopword;

import et.gov.ethgov.search.domain.stopword.Stopword;
import et.gov.ethgov.search.domain.stopword.StopwordRepository;
import java.util.List;
import java.util.Objects;

public final class ListStopwordUseCase {
    private final StopwordRepository repository;

    public ListStopwordUseCase(StopwordRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Stopword> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
