package et.gov.ethgov.case.domain.party;

import java.util.List;
import java.util.Optional;

public interface PartyRepository {
    Party save(Party aggregate);

    Optional<Party> findById(PartyId id);

    Optional<Party> findByReferenceCode(String tenantId, String referenceCode);

    List<Party> findByTenant(String tenantId, int limit);

    void delete(PartyId id);
}
