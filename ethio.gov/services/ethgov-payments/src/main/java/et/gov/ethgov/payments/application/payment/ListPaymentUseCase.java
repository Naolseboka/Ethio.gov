package et.gov.ethgov.payments.application.payment;

import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentRepository;
import java.util.List;
import java.util.Objects;

public final class ListPaymentUseCase {
    private final PaymentRepository repository;

    public ListPaymentUseCase(PaymentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Payment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
