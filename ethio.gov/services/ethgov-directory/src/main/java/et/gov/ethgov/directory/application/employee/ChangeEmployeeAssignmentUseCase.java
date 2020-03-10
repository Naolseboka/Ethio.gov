package et.gov.ethgov.directory.application.employee;

import et.gov.ethgov.directory.application.DirectoryEventPublisher;
import et.gov.ethgov.directory.domain.employee.PublicEmployee;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeId;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeRepository;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitRepository;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeEmployeeAssignmentUseCase {

    private final PublicEmployeeRepository employees;
    private final OrganizationalUnitRepository units;
    private final DirectoryEventPublisher eventPublisher;
    private final Clock clock;

    public ChangeEmployeeAssignmentUseCase(
            PublicEmployeeRepository employees,
            OrganizationalUnitRepository units,
            DirectoryEventPublisher eventPublisher,
            Clock clock) {
        this.employees = employees;
        this.units = units;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String publicEmployeeId, String organizationalUnitId, String jobTitle) {
        Objects.requireNonNull(publicEmployeeId, "publicEmployeeId");
        PublicEmployee employee = employees
                .findById(PublicEmployeeId.of(publicEmployeeId))
                .orElseThrow(() -> new DomainException(
                        "EMPLOYEE_NOT_FOUND",
                        "Employee Not Found",
                        "Public employee does not exist",
                        404));
        OrganizationalUnit unit = units.findById(OrganizationalUnitId.of(organizationalUnitId))
                .orElseThrow(() -> new DomainException(
                        "UNIT_NOT_FOUND",
                        "Unit Not Found",
                        "Organizational unit does not exist",
                        404));
        if (!unit.tenantId().equals(employee.tenantId())) {
            throw new DomainException(
                    "UNIT_TENANT_MISMATCH",
                    "Unit Tenant Mismatch",
                    "Organizational unit belongs to a different tenant",
                    409);
        }
        if (unit.status() != OrganizationalUnitStatus.ACTIVE) {
            throw new DomainException(
                    "UNIT_NOT_ACTIVE",
                    "Unit Not Active",
                    "Employees can only be assigned to active organizational units",
                    409);
        }
        employee.changeAssignment(unit.id(), jobTitle, clock.instant());
        employees.save(employee);
        eventPublisher.publishEmployeeEvents(employee.pullDomainEvents());
    }
}
