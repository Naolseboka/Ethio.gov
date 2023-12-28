package et.gov.ethgov.payments.application.settlement;

import et.gov.ethgov.payments.domain.settlement.Settlement;
import et.gov.ethgov.payments.domain.settlement.SettlementRepository;
import java.util.List;
import java.util.Objects;

public final class ListSettlementUseCase {
    private final SettlementRepository repository;

    public ListSettlementUseCase(SettlementRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Settlement> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
