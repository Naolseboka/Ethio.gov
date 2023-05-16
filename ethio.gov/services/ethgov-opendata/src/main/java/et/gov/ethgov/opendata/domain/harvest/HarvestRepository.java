package et.gov.ethgov.opendata.domain.harvest;

import java.util.List;
import java.util.Optional;

public interface HarvestRepository {
    Harvest save(Harvest aggregate);

    Optional<Harvest> findById(HarvestId id);

    Optional<Harvest> findByReferenceCode(String tenantId, String referenceCode);

    List<Harvest> findByTenant(String tenantId, int limit);

    void delete(HarvestId id);
}
