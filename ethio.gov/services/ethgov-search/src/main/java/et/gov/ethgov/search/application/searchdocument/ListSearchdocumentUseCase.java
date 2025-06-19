package et.gov.ethgov.search.application.searchdocument;

import et.gov.ethgov.search.domain.searchdocument.Searchdocument;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentRepository;
import java.util.List;
import java.util.Objects;

public final class ListSearchdocumentUseCase {
    private final SearchdocumentRepository repository;

    public ListSearchdocumentUseCase(SearchdocumentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Searchdocument> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
