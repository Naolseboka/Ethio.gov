package et.gov.ethgov.organization.domain.orgcontact;

import java.util.List;
import java.util.Optional;

public interface OrgcontactRepository {
    Orgcontact save(Orgcontact aggregate);

    Optional<Orgcontact> findById(OrgcontactId id);

    Optional<Orgcontact> findByReferenceCode(String tenantId, String referenceCode);

    List<Orgcontact> findByTenant(String tenantId, int limit);

    void delete(OrgcontactId id);
}
