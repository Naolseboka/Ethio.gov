package et.gov.ethgov.analytics.application.funnel;

import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelId;
import et.gov.ethgov.analytics.domain.funnel.FunnelRepository;
import java.util.Objects;

public final class GetFunnelUseCase {
    private final FunnelRepository repository;

    public GetFunnelUseCase(FunnelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Funnel execute(String rawId) {
        FunnelId id = FunnelId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("funnel not found: " + rawId));
    }
}
