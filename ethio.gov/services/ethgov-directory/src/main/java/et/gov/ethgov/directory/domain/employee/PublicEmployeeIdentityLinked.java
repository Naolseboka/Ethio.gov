package et.gov.ethgov.directory.domain.employee;

import java.time.Instant;

public record PublicEmployeeIdentityLinked(
        PublicEmployeeId publicEmployeeId, String identityAccountId, Instant occurredAt)
        implements PublicEmployeeDomainEvent {}
