package et.gov.ethgov.revenue.application.bill;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.revenue.application.RevenueEventPublisher;
import et.gov.ethgov.revenue.domain.bill.BillId;
import et.gov.ethgov.revenue.domain.bill.BillRepository;
import et.gov.ethgov.revenue.domain.bill.RevenueBill;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateBillUseCase {

    private final BillRepository bills;
    private final RevenueEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateBillUseCase(BillRepository bills, RevenueEventPublisher eventPublisher, Clock clock) {
        this.bills = bills;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        RevenueBill bill = bills.findById(BillId.of(command.billId()))
                .orElseThrow(() -> new DomainException(
                        "BILL_NOT_FOUND", "Bill Not Found", "Bill does not exist", 404));
        Instant periodStart = parseOptional(command.periodStart());
        Instant periodEnd = parseOptional(command.periodEnd());
        bill.update(
                command.description(),
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
    }

    private static Instant parseOptional(String value) {
        return value == null || value.isBlank() ? null : Instant.parse(value);
    }

    public record Command(
            String billId,
            String description,
            String amountEtb,
            String periodStart,
            String periodEnd,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId) {}
}
