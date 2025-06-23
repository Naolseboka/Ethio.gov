package et.gov.ethgov.search.application.stopword;

import et.gov.ethgov.search.domain.stopword.Stopword;
import et.gov.ethgov.search.domain.stopword.StopwordId;
import et.gov.ethgov.search.domain.stopword.StopwordRepository;
import java.util.Objects;

public final class GetStopwordUseCase {
    private final StopwordRepository repository;

    public GetStopwordUseCase(StopwordRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Stopword execute(String rawId) {
        StopwordId id = StopwordId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("stopword not found: " + rawId));
    }
}
