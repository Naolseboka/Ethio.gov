package et.gov.ethgov.analytics.domain.warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository {
    Warehouse save(Warehouse aggregate);

    Optional<Warehouse> findById(WarehouseId id);

    Optional<Warehouse> findByReferenceCode(String tenantId, String referenceCode);

    List<Warehouse> findByTenant(String tenantId, int limit);

    void delete(WarehouseId id);
}
