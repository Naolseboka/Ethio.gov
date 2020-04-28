package et.gov.ethgov.directory.domain.employee;

import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import java.time.Instant;

public record PublicEmployeeAssignmentChanged(
        PublicEmployeeId publicEmployeeId,
        OrganizationalUnitId organizationalUnitId,
        String jobTitle,
        Instant occurredAt)
        implements PublicEmployeeDomainEvent {}
