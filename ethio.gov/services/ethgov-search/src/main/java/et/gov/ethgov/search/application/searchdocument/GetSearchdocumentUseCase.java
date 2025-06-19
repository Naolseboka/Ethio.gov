package et.gov.ethgov.search.application.searchdocument;

import et.gov.ethgov.search.domain.searchdocument.Searchdocument;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentId;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentRepository;
import java.util.Objects;

public final class GetSearchdocumentUseCase {
    private final SearchdocumentRepository repository;

    public GetSearchdocumentUseCase(SearchdocumentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Searchdocument execute(String rawId) {
        SearchdocumentId id = SearchdocumentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("searchdocument not found: " + rawId));
    }
}
