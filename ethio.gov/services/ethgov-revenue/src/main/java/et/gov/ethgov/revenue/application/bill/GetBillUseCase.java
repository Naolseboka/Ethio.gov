package et.gov.ethgov.revenue.application.bill;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.revenue.domain.bill.BillId;
import et.gov.ethgov.revenue.domain.bill.BillNumber;
import et.gov.ethgov.revenue.domain.bill.BillRepository;
import et.gov.ethgov.revenue.domain.bill.RevenueBill;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetBillUseCase {

    private final BillRepository bills;

    public GetBillUseCase(BillRepository bills) {
        this.bills = bills;
    }

    @Transactional(readOnly = true)
    public RevenueBill byId(String billId) {
        return bills.findById(BillId.of(billId))
                .orElseThrow(() -> new DomainException(
                        "BILL_NOT_FOUND", "Bill Not Found", "Bill does not exist", 404));
    }

    @Transactional(readOnly = true)
    public RevenueBill byNumber(String tenantId, String billNumber) {
        return bills.findByTenantAndBillNumber(TenantId.of(tenantId), BillNumber.of(billNumber))
                .orElseThrow(() -> new DomainException(
                        "BILL_NOT_FOUND", "Bill Not Found", "Bill does not exist", 404));
    }
}
