package et.gov.ethgov.search.application.index;

import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexId;
import et.gov.ethgov.search.domain.index.IndexRepository;
import java.util.Objects;

public final class GetIndexUseCase {
    private final IndexRepository repository;

    public GetIndexUseCase(IndexRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Index execute(String rawId) {
        IndexId id = IndexId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("index not found: " + rawId));
    }
}
