package et.gov.ethgov.directory.application.unit;

import et.gov.ethgov.directory.application.DirectoryEventPublisher;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitRepository;
import et.gov.ethgov.directory.domain.unit.UnitName;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository units;
    private final DirectoryEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateOrganizationalUnitUseCase(
            OrganizationalUnitRepository units, DirectoryEventPublisher eventPublisher, Clock clock) {
        this.units = units;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        OrganizationalUnit unit = units.findById(OrganizationalUnitId.of(command.organizationalUnitId()))
                .orElseThrow(() -> new DomainException(
                        "UNIT_NOT_FOUND",
                        "Unit Not Found",
                        "Organizational unit does not exist",
                        404));
        unit.rename(UnitName.of(command.name()), clock.instant());
        unit.updateDetails(command.linkedOrganizationId(), command.description(), clock.instant());
        units.save(unit);
        eventPublisher.publishUnitEvents(unit.pullDomainEvents());
    }

    public record Command(
            String organizationalUnitId, String name, String linkedOrganizationId, String description) {}
}
