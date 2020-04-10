package et.gov.ethgov.directory.application.unit;

import et.gov.ethgov.directory.application.DirectoryEventPublisher;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitRepository;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitType;
import et.gov.ethgov.directory.domain.unit.UnitCode;
import et.gov.ethgov.directory.domain.unit.UnitName;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository units;
    private final DirectoryEventPublisher eventPublisher;
    private final Clock clock;

    public CreateOrganizationalUnitUseCase(
            OrganizationalUnitRepository units, DirectoryEventPublisher eventPublisher, Clock clock) {
        this.units = units;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public OrganizationalUnitId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        UnitCode code = UnitCode.of(command.code());
        if (units.existsByTenantAndCode(tenantId, code)) {
            throw new DomainException(
                    "UNIT_CODE_EXISTS",
                    "Unit Code Exists",
                    "An organizational unit with this code already exists in the tenant",
                    409);
        }
        OrganizationalUnitId parentId = null;
        if (command.parentId() != null && !command.parentId().isBlank()) {
            OrganizationalUnit parent = units.findById(OrganizationalUnitId.of(command.parentId()))
                    .orElseThrow(() -> new DomainException(
                            "PARENT_NOT_FOUND",
                            "Parent Not Found",
                            "Parent organizational unit does not exist",
                            404));
            if (!parent.tenantId().equals(tenantId)) {
                throw new DomainException(
                        "PARENT_TENANT_MISMATCH",
                        "Parent Tenant Mismatch",
                        "Parent organizational unit belongs to a different tenant",
                        409);
            }
            if (parent.status() != et.gov.ethgov.directory.domain.unit.OrganizationalUnitStatus.ACTIVE) {
                throw new DomainException(
                        "PARENT_NOT_ACTIVE",
                        "Parent Not Active",
                        "Parent organizational unit must be active",
                        409);
            }
            parentId = parent.id();
        }
        OrganizationalUnit unit = OrganizationalUnit.create(
                tenantId,
                code,
                UnitName.of(command.name()),
                OrganizationalUnitType.valueOf(command.type()),
                parentId,
                command.linkedOrganizationId(),
                command.description(),
                clock.instant());
        units.save(unit);
        eventPublisher.publishUnitEvents(unit.pullDomainEvents());
        return unit.id();
    }

    public record Command(
            String tenantId,
            String code,
            String name,
            String type,
            String parentId,
            String linkedOrganizationId,
            String description) {}
}
