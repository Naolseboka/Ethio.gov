package et.gov.ethgov.directory.domain.employee;

import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record PublicEmployeeRegistered(
        PublicEmployeeId publicEmployeeId,
        TenantId tenantId,
        OrganizationalUnitId organizationalUnitId,
        EmployeeNumber employeeNumber,
        Instant occurredAt)
        implements PublicEmployeeDomainEvent {}
