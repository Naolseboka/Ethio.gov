package et.gov.ethgov.land.application.valuation;

import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationRepository;
import java.util.List;
import java.util.Objects;

public final class ListValuationUseCase {
    private final ValuationRepository repository;

    public ListValuationUseCase(ValuationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Valuation> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
