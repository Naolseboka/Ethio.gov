package et.gov.ethgov.developer.application.devratelimit;

import et.gov.ethgov.developer.domain.devratelimit.Devratelimit;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitId;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitRepository;
import java.util.Objects;

public final class GetDevratelimitUseCase {
    private final DevratelimitRepository repository;

    public GetDevratelimitUseCase(DevratelimitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Devratelimit execute(String rawId) {
        DevratelimitId id = DevratelimitId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devratelimit not found: " + rawId));
    }
}
