package et.gov.ethgov.directory.application.employee;

import et.gov.ethgov.directory.domain.employee.EmployeeNumber;
import et.gov.ethgov.directory.domain.employee.PublicEmployee;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeId;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPublicEmployeeUseCase {

    private final PublicEmployeeRepository employees;

    public GetPublicEmployeeUseCase(PublicEmployeeRepository employees) {
        this.employees = employees;
    }

    @Transactional(readOnly = true)
    public PublicEmployee byId(String publicEmployeeId) {
        return employees
                .findById(PublicEmployeeId.of(publicEmployeeId))
                .orElseThrow(() -> new DomainException(
                        "EMPLOYEE_NOT_FOUND",
                        "Employee Not Found",
                        "Public employee does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public PublicEmployee byTenantAndNumber(String tenantId, String employeeNumber) {
        return employees
                .findByTenantAndEmployeeNumber(TenantId.of(tenantId), EmployeeNumber.of(employeeNumber))
                .orElseThrow(() -> new DomainException(
                        "EMPLOYEE_NOT_FOUND",
                        "Employee Not Found",
                        "Public employee does not exist",
                        404));
    }
}
