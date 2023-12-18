package et.gov.ethgov.payments.application.paymentplan;

import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanRepository;
import java.util.List;
import java.util.Objects;

public final class ListPaymentplanUseCase {
    private final PaymentplanRepository repository;

    public ListPaymentplanUseCase(PaymentplanRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Paymentplan> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
