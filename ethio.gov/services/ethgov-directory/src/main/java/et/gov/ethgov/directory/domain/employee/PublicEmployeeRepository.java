package et.gov.ethgov.directory.domain.employee;

import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface PublicEmployeeRepository {

    boolean existsByTenantAndEmployeeNumber(TenantId tenantId, EmployeeNumber employeeNumber);

    boolean existsActiveByOrganizationalUnit(OrganizationalUnitId organizationalUnitId);

    Optional<PublicEmployee> findById(PublicEmployeeId id);

    Optional<PublicEmployee> findByTenantAndEmployeeNumber(TenantId tenantId, EmployeeNumber employeeNumber);

    void save(PublicEmployee employee);
}
