package et.gov.ethgov.revenue.domain.arrears;

import java.util.List;
import java.util.Optional;

public interface ArrearsRepository {
    Arrears save(Arrears aggregate);

    Optional<Arrears> findById(ArrearsId id);

    Optional<Arrears> findByReferenceCode(String tenantId, String referenceCode);

    List<Arrears> findByTenant(String tenantId, int limit);

    void delete(ArrearsId id);
}
