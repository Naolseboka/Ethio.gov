package et.gov.ethgov.revenue.application.penalty;

import et.gov.ethgov.revenue.domain.penalty.Penalty;
import et.gov.ethgov.revenue.domain.penalty.PenaltyRepository;
import java.util.List;
import java.util.Objects;

public final class ListPenaltyUseCase {
    private final PenaltyRepository repository;

    public ListPenaltyUseCase(PenaltyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Penalty> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
