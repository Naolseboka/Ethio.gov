package et.gov.ethgov.revenue.domain.bill;

import et.gov.ethgov.platform.commons.tenant.TenantId;

import java.util.Optional;

public interface BillRepository {
    void save(RevenueBill bill);
    Optional<RevenueBill> findById(BillId billId);
    Optional<RevenueBill> findByTenantAndBillNumber(TenantId tenantId, BillNumber billNumber);
    boolean existsByTenantAndBillNumber(TenantId tenantId, BillNumber billNumber);
}
