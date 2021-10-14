package et.gov.ethgov.identity.application.riskscore;

import et.gov.ethgov.identity.domain.riskscore.Riskscore;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreRepository;
import java.util.List;
import java.util.Objects;

public final class ListRiskscoreUseCase {
    private final RiskscoreRepository repository;

    public ListRiskscoreUseCase(RiskscoreRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Riskscore> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
