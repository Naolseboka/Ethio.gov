package et.gov.ethgov.search.domain.suggestion;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository {
    Suggestion save(Suggestion aggregate);

    Optional<Suggestion> findById(SuggestionId id);

    Optional<Suggestion> findByReferenceCode(String tenantId, String referenceCode);

    List<Suggestion> findByTenant(String tenantId, int limit);

    void delete(SuggestionId id);
}
