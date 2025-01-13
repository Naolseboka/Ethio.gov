package et.gov.ethgov.revenue.application.bill;

import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import et.gov.ethgov.revenue.application.RevenueEventPublisher;
import et.gov.ethgov.revenue.domain.bill.BillId;
import et.gov.ethgov.revenue.domain.bill.BillNumber;
import et.gov.ethgov.revenue.domain.bill.BillNumberSequence;
import et.gov.ethgov.revenue.domain.bill.BillParty;
import et.gov.ethgov.revenue.domain.bill.BillRepository;
import et.gov.ethgov.revenue.domain.bill.RevenueBill;
import et.gov.ethgov.revenue.domain.bill.RevenueType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBillUseCase {

    private final BillRepository bills;
    private final BillNumberSequence sequences;
    private final RevenueEventPublisher eventPublisher;
    private final Clock clock;

    public CreateBillUseCase(
            BillRepository bills,
            BillNumberSequence sequences,
            RevenueEventPublisher eventPublisher,
            Clock clock) {
        this.bills = bills;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public BillId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        BillNumber billNumber = BillNumber.allocate(year, sequences.nextSequence(tenantId, year));
        Instant periodStart = parseOptional(command.periodStart());
        Instant periodEnd = parseOptional(command.periodEnd());
        RevenueBill bill = RevenueBill.create(
                tenantId,
                billNumber,
                RevenueType.valueOf(command.type()),
                command.description(),
                BillParty.of(command.citizenId(), command.organizationId()),
                Money.etb(new BigDecimal(command.amountEtb())),
                periodStart,
                periodEnd,
                command.licenseId(),
                command.permitId(),
                command.parcelId(),
                command.linkedCaseId(),
                clock.instant());
        bills.save(bill);
        eventPublisher.publishAll(bill.pullDomainEvents());
        return bill.id();
    }

    private static Instant parseOptional(String value) {
        return value == null || value.isBlank() ? null : Instant.parse(value);
    }

    public record Command(
            String tenantId,
            String type,
            String description,
            String citizenId,
            String organizationId,
            String amountEtb,
            String periodStart,
            String periodEnd,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId) {}
}
