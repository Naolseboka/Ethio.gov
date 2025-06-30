package et.gov.ethgov.search.application.synonym;

import et.gov.ethgov.search.domain.synonym.Synonym;
import et.gov.ethgov.search.domain.synonym.SynonymRepository;
import java.util.List;
import java.util.Objects;

public final class ListSynonymUseCase {
    private final SynonymRepository repository;

    public ListSynonymUseCase(SynonymRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Synonym> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
