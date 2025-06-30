package et.gov.ethgov.search.application.synonym;

import et.gov.ethgov.search.domain.synonym.Synonym;
import et.gov.ethgov.search.domain.synonym.SynonymId;
import et.gov.ethgov.search.domain.synonym.SynonymRepository;
import java.util.Objects;

public final class GetSynonymUseCase {
    private final SynonymRepository repository;

    public GetSynonymUseCase(SynonymRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Synonym execute(String rawId) {
        SynonymId id = SynonymId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("synonym not found: " + rawId));
    }
}
