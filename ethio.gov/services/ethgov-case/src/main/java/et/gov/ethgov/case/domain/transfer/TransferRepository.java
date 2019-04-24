package et.gov.ethgov.case.domain.transfer;

import java.util.List;
import java.util.Optional;

public interface TransferRepository {
    Transfer save(Transfer aggregate);

    Optional<Transfer> findById(TransferId id);

    Optional<Transfer> findByReferenceCode(String tenantId, String referenceCode);

    List<Transfer> findByTenant(String tenantId, int limit);

    void delete(TransferId id);
}
