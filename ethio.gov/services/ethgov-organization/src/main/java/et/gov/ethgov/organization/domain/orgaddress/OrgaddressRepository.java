package et.gov.ethgov.organization.domain.orgaddress;

import java.util.List;
import java.util.Optional;

public interface OrgaddressRepository {
    Orgaddress save(Orgaddress aggregate);

    Optional<Orgaddress> findById(OrgaddressId id);

    Optional<Orgaddress> findByReferenceCode(String tenantId, String referenceCode);

    List<Orgaddress> findByTenant(String tenantId, int limit);

    void delete(OrgaddressId id);
}
