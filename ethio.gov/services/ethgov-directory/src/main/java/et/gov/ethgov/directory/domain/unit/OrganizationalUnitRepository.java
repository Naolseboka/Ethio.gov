package et.gov.ethgov.directory.domain.unit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import java.util.Optional;

public interface OrganizationalUnitRepository {

    boolean existsByTenantAndCode(TenantId tenantId, UnitCode code);

    Optional<OrganizationalUnit> findById(OrganizationalUnitId id);

    Optional<OrganizationalUnit> findByTenantAndCode(TenantId tenantId, UnitCode code);

    List<OrganizationalUnit> findChildren(OrganizationalUnitId parentId);

    long countActiveChildren(OrganizationalUnitId parentId);

    void save(OrganizationalUnit unit);
}
