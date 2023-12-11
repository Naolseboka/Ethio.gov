package et.gov.ethgov.payments.application.payment;

import et.gov.ethgov.payments.application.PaymentsEventPublisher;
import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentId;
import et.gov.ethgov.payments.domain.payment.PaymentMethod;
import et.gov.ethgov.payments.domain.payment.PaymentNumber;
import et.gov.ethgov.payments.domain.payment.PaymentNumberSequence;
import et.gov.ethgov.payments.domain.payment.PaymentParty;
import et.gov.ethgov.payments.domain.payment.PaymentRepository;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InitiatePaymentUseCase {

    private final PaymentRepository payments;
    private final PaymentNumberSequence sequences;
    private final PaymentsEventPublisher eventPublisher;
    private final Clock clock;

    public InitiatePaymentUseCase(
            PaymentRepository payments,
            PaymentNumberSequence sequences,
            PaymentsEventPublisher eventPublisher,
            Clock clock) {
        this.payments = payments;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public PaymentId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        PaymentNumber paymentNumber = PaymentNumber.allocate(year, sequences.nextSequence(tenantId, year));
        Payment payment = Payment.initiate(
                tenantId,
                paymentNumber,
                command.billId(),
                PaymentMethod.valueOf(command.method()),
                PaymentParty.of(command.citizenId(), command.organizationId()),
                Money.etb(new BigDecimal(command.amountEtb())),
                command.idempotencyKey(),
                clock.instant());
        payments.save(payment);
        eventPublisher.publishAll(payment.pullDomainEvents());
        return payment.id();
    }

    public record Command(
            String tenantId,
            String billId,
            String method,
            String citizenId,
            String organizationId,
            String amountEtb,
            String idempotencyKey) {}
}
