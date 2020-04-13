package et.gov.ethgov.directory.application.unit;

import et.gov.ethgov.directory.application.DirectoryEventPublisher;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeRepository;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository units;
    private final PublicEmployeeRepository employees;
    private final DirectoryEventPublisher eventPublisher;
    private final Clock clock;

    public DeactivateOrganizationalUnitUseCase(
            OrganizationalUnitRepository units,
            PublicEmployeeRepository employees,
            DirectoryEventPublisher eventPublisher,
            Clock clock) {
        this.units = units;
        this.employees = employees;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String organizationalUnitId) {
        Objects.requireNonNull(organizationalUnitId, "organizationalUnitId");
        OrganizationalUnitId id = OrganizationalUnitId.of(organizationalUnitId);
        OrganizationalUnit unit = units.findById(id)
                .orElseThrow(() -> new DomainException(
                        "UNIT_NOT_FOUND",
                        "Unit Not Found",
                        "Organizational unit does not exist",
                        404));
        if (units.countActiveChildren(id) > 0) {
            throw new DomainException(
                    "UNIT_HAS_ACTIVE_CHILDREN",
                    "Unit Has Active Children",
                    "Deactivate or move child units before deactivating this unit",
                    409);
        }
        if (employees.existsActiveByOrganizationalUnit(id)) {
            throw new DomainException(
                    "UNIT_HAS_ACTIVE_EMPLOYEES",
                    "Unit Has Active Employees",
                    "Reassign or separate employees before deactivating this unit",
                    409);
        }
        unit.deactivate(clock.instant());
        units.save(unit);
        eventPublisher.publishUnitEvents(unit.pullDomainEvents());
    }
}
