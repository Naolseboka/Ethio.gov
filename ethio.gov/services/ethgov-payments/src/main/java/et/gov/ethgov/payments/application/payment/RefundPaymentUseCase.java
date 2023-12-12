package et.gov.ethgov.payments.application.payment;

import et.gov.ethgov.payments.application.PaymentsEventPublisher;
import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentId;
import et.gov.ethgov.payments.domain.payment.PaymentRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundPaymentUseCase {

    private final PaymentRepository payments;
    private final PaymentsEventPublisher eventPublisher;
    private final Clock clock;

    public RefundPaymentUseCase(PaymentRepository payments, PaymentsEventPublisher eventPublisher, Clock clock) {
        this.payments = payments;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String paymentId, String reason) {
        Objects.requireNonNull(paymentId, "paymentId");
        Payment payment = payments.findById(PaymentId.of(paymentId))
                .orElseThrow(() -> new DomainException(
                        "PAYMENT_NOT_FOUND", "Payment Not Found", "Payment does not exist", 404));
        payment.refund(reason, clock.instant());
        payments.save(payment);
        eventPublisher.publishAll(payment.pullDomainEvents());
    }
}
