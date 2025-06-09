package et.gov.ethgov.search.application.query;

import et.gov.ethgov.search.domain.query.Query;
import et.gov.ethgov.search.domain.query.QueryId;
import et.gov.ethgov.search.domain.query.QueryRepository;
import java.util.Objects;

public final class GetQueryUseCase {
    private final QueryRepository repository;

    public GetQueryUseCase(QueryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Query execute(String rawId) {
        QueryId id = QueryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("query not found: " + rawId));
    }
}
