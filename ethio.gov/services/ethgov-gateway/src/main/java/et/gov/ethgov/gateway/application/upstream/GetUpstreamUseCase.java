package et.gov.ethgov.gateway.application.upstream;

import et.gov.ethgov.gateway.domain.upstream.Upstream;
import et.gov.ethgov.gateway.domain.upstream.UpstreamId;
import et.gov.ethgov.gateway.domain.upstream.UpstreamRepository;
import java.util.Objects;

public final class GetUpstreamUseCase {
    private final UpstreamRepository repository;

    public GetUpstreamUseCase(UpstreamRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Upstream execute(String rawId) {
        UpstreamId id = UpstreamId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("upstream not found: " + rawId));
    }
}
