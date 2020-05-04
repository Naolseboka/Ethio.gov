package et.gov.ethgov.directory.domain.employee;

import java.time.Instant;
import java.time.LocalDate;

public record PublicEmployeeSeparated(
        PublicEmployeeId publicEmployeeId, LocalDate separatedOn, Instant occurredAt)
        implements PublicEmployeeDomainEvent {}
