package et.gov.ethgov.organization.domain.orgunit;

import java.util.List;
import java.util.Optional;

public interface OrgunitRepository {
    Orgunit save(Orgunit aggregate);

    Optional<Orgunit> findById(OrgunitId id);

    Optional<Orgunit> findByReferenceCode(String tenantId, String referenceCode);

    List<Orgunit> findByTenant(String tenantId, int limit);

    void delete(OrgunitId id);
}
