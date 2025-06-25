package et.gov.ethgov.search.application.suggestion;

import et.gov.ethgov.search.domain.suggestion.Suggestion;
import et.gov.ethgov.search.domain.suggestion.SuggestionId;
import et.gov.ethgov.search.domain.suggestion.SuggestionRepository;
import java.util.Objects;

public final class GetSuggestionUseCase {
    private final SuggestionRepository repository;

    public GetSuggestionUseCase(SuggestionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Suggestion execute(String rawId) {
        SuggestionId id = SuggestionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("suggestion not found: " + rawId));
    }
}
