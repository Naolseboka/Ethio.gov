package et.gov.ethgov.permits.domain.sitevisit;

import java.util.List;
import java.util.Optional;

public interface SitevisitRepository {
    Sitevisit save(Sitevisit aggregate);

    Optional<Sitevisit> findById(SitevisitId id);

    Optional<Sitevisit> findByReferenceCode(String tenantId, String referenceCode);

    List<Sitevisit> findByTenant(String tenantId, int limit);

    void delete(SitevisitId id);
}
