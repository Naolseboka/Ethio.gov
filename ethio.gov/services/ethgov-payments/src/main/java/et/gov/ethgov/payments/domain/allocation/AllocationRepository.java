package et.gov.ethgov.payments.domain.allocation;

import java.util.List;
import java.util.Optional;

public interface AllocationRepository {
    Allocation save(Allocation aggregate);

    Optional<Allocation> findById(AllocationId id);

    Optional<Allocation> findByReferenceCode(String tenantId, String referenceCode);

    List<Allocation> findByTenant(String tenantId, int limit);

    void delete(AllocationId id);
}
