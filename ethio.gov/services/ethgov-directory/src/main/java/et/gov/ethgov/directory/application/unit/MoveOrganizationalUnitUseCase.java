package et.gov.ethgov.directory.application.unit;

import et.gov.ethgov.directory.application.DirectoryEventPublisher;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitRepository;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoveOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository units;
    private final DirectoryEventPublisher eventPublisher;
    private final Clock clock;

    public MoveOrganizationalUnitUseCase(
            OrganizationalUnitRepository units, DirectoryEventPublisher eventPublisher, Clock clock) {
        this.units = units;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String organizationalUnitId, String newParentIdRaw) {
        Objects.requireNonNull(organizationalUnitId, "organizationalUnitId");
        OrganizationalUnit unit = units.findById(OrganizationalUnitId.of(organizationalUnitId))
                .orElseThrow(() -> new DomainException(
                        "UNIT_NOT_FOUND",
                        "Unit Not Found",
                        "Organizational unit does not exist",
                        404));
        OrganizationalUnitId newParentId = null;
        if (newParentIdRaw != null && !newParentIdRaw.isBlank()) {
            OrganizationalUnit parent = units.findById(OrganizationalUnitId.of(newParentIdRaw))
                    .orElseThrow(() -> new DomainException(
                            "PARENT_NOT_FOUND",
                            "Parent Not Found",
                            "Parent organizational unit does not exist",
                            404));
            if (!parent.tenantId().equals(unit.tenantId())) {
                throw new DomainException(
                        "PARENT_TENANT_MISMATCH",
                        "Parent Tenant Mismatch",
                        "Parent organizational unit belongs to a different tenant",
                        409);
            }
            if (parent.status() != OrganizationalUnitStatus.ACTIVE) {
                throw new DomainException(
                        "PARENT_NOT_ACTIVE",
                        "Parent Not Active",
                        "Parent organizational unit must be active",
                        409);
            }
            assertNoCycle(unit.id(), parent);
            newParentId = parent.id();
        }
        unit.moveTo(newParentId, clock.instant());
        units.save(unit);
        eventPublisher.publishUnitEvents(unit.pullDomainEvents());
    }

    private void assertNoCycle(OrganizationalUnitId movingId, OrganizationalUnit candidateParent) {
        Set<String> visited = new HashSet<>();
        OrganizationalUnit current = candidateParent;
        while (current != null) {
            if (current.id().equals(movingId)) {
                throw new DomainException(
                        "HIERARCHY_CYCLE",
                        "Hierarchy Cycle",
                        "Moving the unit would create a cycle in the hierarchy",
                        409);
            }
            if (!visited.add(current.id().value())) {
                break;
            }
            Optional<OrganizationalUnitId> parentId = current.parentId();
            current = parentId.flatMap(units::findById).orElse(null);
        }
    }
}
