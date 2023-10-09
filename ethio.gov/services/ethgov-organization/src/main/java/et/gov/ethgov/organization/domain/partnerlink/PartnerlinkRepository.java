package et.gov.ethgov.organization.domain.partnerlink;

import java.util.List;
import java.util.Optional;

public interface PartnerlinkRepository {
    Partnerlink save(Partnerlink aggregate);

    Optional<Partnerlink> findById(PartnerlinkId id);

    Optional<Partnerlink> findByReferenceCode(String tenantId, String referenceCode);

    List<Partnerlink> findByTenant(String tenantId, int limit);

    void delete(PartnerlinkId id);
}
