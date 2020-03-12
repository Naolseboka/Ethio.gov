package et.gov.ethgov.directory.application.employee;

import et.gov.ethgov.directory.application.DirectoryEventPublisher;
import et.gov.ethgov.directory.domain.employee.PublicEmployee;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeId;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkEmployeeIdentityUseCase {

    private final PublicEmployeeRepository employees;
    private final DirectoryEventPublisher eventPublisher;
    private final Clock clock;

    public LinkEmployeeIdentityUseCase(
            PublicEmployeeRepository employees, DirectoryEventPublisher eventPublisher, Clock clock) {
        this.employees = employees;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String publicEmployeeId, String identityAccountId) {
        Objects.requireNonNull(publicEmployeeId, "publicEmployeeId");
        PublicEmployee employee = employees
                .findById(PublicEmployeeId.of(publicEmployeeId))
                .orElseThrow(() -> new DomainException(
                        "EMPLOYEE_NOT_FOUND",
                        "Employee Not Found",
                        "Public employee does not exist",
                        404));
        employee.linkIdentityAccount(identityAccountId, clock.instant());
        employees.save(employee);
        eventPublisher.publishEmployeeEvents(employee.pullDomainEvents());
    }
}
