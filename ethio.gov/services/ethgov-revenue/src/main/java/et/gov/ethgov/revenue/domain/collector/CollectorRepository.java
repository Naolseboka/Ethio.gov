package et.gov.ethgov.revenue.domain.collector;

import java.util.List;
import java.util.Optional;

public interface CollectorRepository {
    Collector save(Collector aggregate);

    Optional<Collector> findById(CollectorId id);

    Optional<Collector> findByReferenceCode(String tenantId, String referenceCode);

    List<Collector> findByTenant(String tenantId, int limit);

    void delete(CollectorId id);
}
