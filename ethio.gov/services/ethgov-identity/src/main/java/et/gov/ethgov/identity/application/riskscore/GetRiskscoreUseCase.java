package et.gov.ethgov.identity.application.riskscore;

import et.gov.ethgov.identity.domain.riskscore.Riskscore;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreId;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreRepository;
import java.util.Objects;

public final class GetRiskscoreUseCase {
    private final RiskscoreRepository repository;

    public GetRiskscoreUseCase(RiskscoreRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Riskscore execute(String rawId) {
        RiskscoreId id = RiskscoreId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("riskscore not found: " + rawId));
    }
}
