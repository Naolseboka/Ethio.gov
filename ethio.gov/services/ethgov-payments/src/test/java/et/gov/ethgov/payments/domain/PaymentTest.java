package et.gov.ethgov.payments.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentInitiated;
import et.gov.ethgov.payments.domain.payment.PaymentMethod;
import et.gov.ethgov.payments.domain.payment.PaymentNumber;
import et.gov.ethgov.payments.domain.payment.PaymentParty;
import et.gov.ethgov.payments.domain.payment.PaymentStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PaymentTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final String org = "org_11111111-1111-4111-8111-111111111111";
    private final String bill = "rev_11111111-1111-4111-8111-111111111111";

    @Test
    void initiatesSucceedsAndRefunds() {
        Payment payment = initiate();
        assertThat(payment.status()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(payment.pullDomainEvents()).anyMatch(PaymentInitiated.class::isInstance);

        payment.succeed("TELEBIRR-TX-9001", now);
        assertThat(payment.status()).isEqualTo(PaymentStatus.SUCCEEDED);

        payment.refund("Customer requested refund", now.plusSeconds(60));
        assertThat(payment.status()).isEqualTo(PaymentStatus.REFUNDED);
        assertThatThrownBy(() -> payment.succeed("again", now.plusSeconds(120)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void requiresRevenueBillId() {
        assertThatThrownBy(() -> Payment.initiate(
                        TenantId.generate(),
                        PaymentNumber.allocate(2024, 1),
                        "bad",
                        PaymentMethod.CASH,
                        PaymentParty.of(null, org),
                        Money.etb("100.00"),
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Payment initiate() {
        return Payment.initiate(
                TenantId.generate(),
                PaymentNumber.allocate(2024, 1),
                bill,
                PaymentMethod.MOBILE_MONEY,
                PaymentParty.of(null, org),
                Money.etb("1750.00"),
                "idem-license-fee-001",
                now);
    }
}
