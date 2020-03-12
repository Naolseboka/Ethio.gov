package et.gov.ethgov.directory.application.employee;

import et.gov.ethgov.directory.application.DirectoryEventPublisher;
import et.gov.ethgov.directory.domain.employee.EmployeeNumber;
import et.gov.ethgov.directory.domain.employee.PublicEmployee;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeId;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeRepository;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitRepository;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterPublicEmployeeUseCase {

    private final PublicEmployeeRepository employees;
    private final OrganizationalUnitRepository units;
    private final DirectoryEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterPublicEmployeeUseCase(
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
    public PublicEmployeeId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        EmployeeNumber employeeNumber = EmployeeNumber.of(command.employeeNumber());
        if (employees.existsByTenantAndEmployeeNumber(tenantId, employeeNumber)) {
            throw new DomainException(
                    "EMPLOYEE_NUMBER_EXISTS",
                    "Employee Number Exists",
                    "An employee with this number already exists in the tenant",
                    409);
        }
        OrganizationalUnit unit = units.findById(OrganizationalUnitId.of(command.organizationalUnitId()))
                .orElseThrow(() -> new DomainException(
                        "UNIT_NOT_FOUND",
                        "Unit Not Found",
                        "Organizational unit does not exist",
                        404));
        if (!unit.tenantId().equals(tenantId)) {
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
        LocalDate hiredOn = LocalDate.parse(command.hiredOn());
        PublicEmployee employee = PublicEmployee.register(
                tenantId,
                employeeNumber,
                command.citizenId(),
                command.displayName(),
                unit.id(),
                command.jobTitle(),
                hiredOn,
                LocalDate.now(EthGovClocks.ADDIS_ABABA),
                clock.instant());
        employees.save(employee);
        eventPublisher.publishEmployeeEvents(employee.pullDomainEvents());
        return employee.id();
    }

    public record Command(
            String tenantId,
            String employeeNumber,
            String citizenId,
            String displayName,
            String organizationalUnitId,
            String jobTitle,
            String hiredOn) {}
}
