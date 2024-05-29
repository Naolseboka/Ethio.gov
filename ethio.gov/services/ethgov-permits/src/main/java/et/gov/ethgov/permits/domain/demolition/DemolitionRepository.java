package et.gov.ethgov.permits.domain.demolition;

import java.util.List;
import java.util.Optional;

public interface DemolitionRepository {
    Demolition save(Demolition aggregate);

    Optional<Demolition> findById(DemolitionId id);

    Optional<Demolition> findByReferenceCode(String tenantId, String referenceCode);

    List<Demolition> findByTenant(String tenantId, int limit);

    void delete(DemolitionId id);
}
