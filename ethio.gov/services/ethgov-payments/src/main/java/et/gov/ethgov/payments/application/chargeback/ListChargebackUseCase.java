package et.gov.ethgov.payments.application.chargeback;

import et.gov.ethgov.payments.domain.chargeback.Chargeback;
import et.gov.ethgov.payments.domain.chargeback.ChargebackRepository;
import java.util.List;
import java.util.Objects;

public final class ListChargebackUseCase {
    private final ChargebackRepository repository;

    public ListChargebackUseCase(ChargebackRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Chargeback> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
