package et.gov.ethgov.analytics.application.scorecard;

import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardId;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardRepository;
import java.util.Objects;

public final class GetScorecardUseCase {
    private final ScorecardRepository repository;

    public GetScorecardUseCase(ScorecardRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Scorecard execute(String rawId) {
        ScorecardId id = ScorecardId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("scorecard not found: " + rawId));
    }
}
