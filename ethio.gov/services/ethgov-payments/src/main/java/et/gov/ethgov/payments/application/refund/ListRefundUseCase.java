package et.gov.ethgov.payments.application.refund;

import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundRepository;
import java.util.List;
import java.util.Objects;

public final class ListRefundUseCase {
    private final RefundRepository repository;

    public ListRefundUseCase(RefundRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Refund> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
