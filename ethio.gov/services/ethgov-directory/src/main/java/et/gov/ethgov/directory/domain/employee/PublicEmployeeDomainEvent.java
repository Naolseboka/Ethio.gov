package et.gov.ethgov.directory.domain.employee;

import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import java.time.Instant;

public sealed interface PublicEmployeeDomainEvent
        permits PublicEmployeeRegistered,
                PublicEmployeeAssignmentChanged,
                PublicEmployeeIdentityLinked,
                PublicEmployeeSeparated {

    PublicEmployeeId publicEmployeeId();

    Instant occurredAt();
}
