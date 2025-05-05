package et.gov.ethgov.revenue.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.revenue.domain.bill.BillCreated;
import et.gov.ethgov.revenue.domain.bill.BillNumber;
import et.gov.ethgov.revenue.domain.bill.BillParty;
import et.gov.ethgov.revenue.domain.bill.BillStatus;
import et.gov.ethgov.revenue.domain.bill.RevenueBill;
import et.gov.ethgov.revenue.domain.bill.RevenueType;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class RevenueBillTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final String org = "org_11111111-1111-4111-8111-111111111111";

    @Test
    void createsIssuesMarksOverdueAndPays() {
        RevenueBill bill = create();
        assertThat(bill.status()).isEqualTo(BillStatus.DRAFT);
        assertThat(bill.pullDomainEvents()).anyMatch(BillCreated.class::isInstance);

        Instant dueAt = Instant.parse("2024-07-01T00:00:00Z");
        bill.issue(dueAt, now);
        assertThat(bill.status()).isEqualTo(BillStatus.ISSUED);

        bill.markOverdue(Instant.parse("2024-07-02T00:00:00Z"));
        assertThat(bill.status()).isEqualTo(BillStatus.OVERDUE);

        bill.markPaid("pay_11111111-1111-4111-8111-111111111111", Instant.parse("2024-07-03T00:00:00Z"));
        assertThat(bill.status()).isEqualTo(BillStatus.PAID);
        assertThatThrownBy(() -> bill.cancel("too late", Instant.parse("2024-07-04T00:00:00Z")))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThatThrownBy(() -> RevenueBill.create(
                        TenantId.generate(),
                        BillNumber.allocate(2024, 1),
                        RevenueType.LICENSE_FEE,
                        "Trade license fee",
                        BillParty.of(null, org),
                        Money.etb("0.00"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private RevenueBill create() {
        return RevenueBill.create(
                TenantId.generate(),
                BillNumber.allocate(2024, 1),
                RevenueType.LICENSE_FEE,
                "Annual trade license fee",
                BillParty.of(null, org),
                Money.etb("1500.00"),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-12-31T00:00:00Z"),
                "lic_11111111-1111-4111-8111-111111111111",
                null,
                null,
                null,
                now);
    }
}
