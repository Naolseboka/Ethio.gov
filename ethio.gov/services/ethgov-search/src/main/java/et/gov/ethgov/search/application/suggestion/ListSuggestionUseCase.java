package et.gov.ethgov.search.application.suggestion;

import et.gov.ethgov.search.domain.suggestion.Suggestion;
import et.gov.ethgov.search.domain.suggestion.SuggestionRepository;
import java.util.List;
import java.util.Objects;

public final class ListSuggestionUseCase {
    private final SuggestionRepository repository;

    public ListSuggestionUseCase(SuggestionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Suggestion> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
