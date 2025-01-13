package et.gov.ethgov.revenue.application.bill;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.revenue.application.RevenueEventPublisher;
import et.gov.ethgov.revenue.domain.bill.BillId;
import et.gov.ethgov.revenue.domain.bill.BillRepository;
import et.gov.ethgov.revenue.domain.bill.RevenueBill;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelBillUseCase {

    private final BillRepository bills;
    private final RevenueEventPublisher eventPublisher;
    private final Clock clock;

    public CancelBillUseCase(BillRepository bills, RevenueEventPublisher eventPublisher, Clock clock) {
        this.bills = bills;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String billId, String reason) {
        Objects.requireNonNull(billId, "billId");
        RevenueBill bill = bills.findById(BillId.of(billId))
                .orElseThrow(() -> new DomainException(
                        "BILL_NOT_FOUND", "Bill Not Found", "Bill does not exist", 404));
        bill.cancel(reason, clock.instant());
        bills.save(bill);
        eventPublisher.publishAll(bill.pullDomainEvents());
    }
}
