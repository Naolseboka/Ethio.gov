package et.gov.ethgov.gateway.application.rewrite;

import et.gov.ethgov.gateway.domain.rewrite.Rewrite;
import et.gov.ethgov.gateway.domain.rewrite.RewriteId;
import et.gov.ethgov.gateway.domain.rewrite.RewriteRepository;
import java.util.Objects;

public final class GetRewriteUseCase {
    private final RewriteRepository repository;

    public GetRewriteUseCase(RewriteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Rewrite execute(String rawId) {
        RewriteId id = RewriteId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("rewrite not found: " + rawId));
    }
}
