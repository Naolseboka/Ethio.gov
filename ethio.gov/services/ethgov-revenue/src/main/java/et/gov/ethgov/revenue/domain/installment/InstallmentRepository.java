package et.gov.ethgov.revenue.domain.installment;

import java.util.List;
import java.util.Optional;

public interface InstallmentRepository {
    Installment save(Installment aggregate);

    Optional<Installment> findById(InstallmentId id);

    Optional<Installment> findByReferenceCode(String tenantId, String referenceCode);

    List<Installment> findByTenant(String tenantId, int limit);

    void delete(InstallmentId id);
}
