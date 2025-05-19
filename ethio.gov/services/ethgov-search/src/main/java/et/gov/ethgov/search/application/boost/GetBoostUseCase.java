package et.gov.ethgov.search.application.boost;

import et.gov.ethgov.search.domain.boost.Boost;
import et.gov.ethgov.search.domain.boost.BoostId;
import et.gov.ethgov.search.domain.boost.BoostRepository;
import java.util.Objects;

public final class GetBoostUseCase {
    private final BoostRepository repository;

    public GetBoostUseCase(BoostRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Boost execute(String rawId) {
        BoostId id = BoostId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("boost not found: " + rawId));
    }
}
