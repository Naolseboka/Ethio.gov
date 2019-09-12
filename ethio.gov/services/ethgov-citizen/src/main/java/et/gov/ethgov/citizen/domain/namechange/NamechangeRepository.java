package et.gov.ethgov.citizen.domain.namechange;

import java.util.List;
import java.util.Optional;

public interface NamechangeRepository {
    Namechange save(Namechange aggregate);

    Optional<Namechange> findById(NamechangeId id);

    Optional<Namechange> findByReferenceCode(String tenantId, String referenceCode);

    List<Namechange> findByTenant(String tenantId, int limit);

    void delete(NamechangeId id);
}
