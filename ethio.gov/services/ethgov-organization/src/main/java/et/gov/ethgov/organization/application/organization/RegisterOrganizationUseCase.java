package et.gov.ethgov.organization.application.organization;

import et.gov.ethgov.organization.domain.organization.LegalName;
import et.gov.ethgov.organization.domain.organization.Organization;
import et.gov.ethgov.organization.domain.organization.OrganizationId;
import et.gov.ethgov.organization.domain.organization.OrganizationRepository;
import et.gov.ethgov.organization.domain.organization.OrganizationType;
import et.gov.ethgov.platform.commons.address.EthiopianAddress;
import et.gov.ethgov.platform.commons.address.RegionalState;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterOrganizationUseCase {

    private final OrganizationRepository organizations;
    private final OrganizationEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterOrganizationUseCase(
            OrganizationRepository organizations, OrganizationEventPublisher eventPublisher, Clock clock) {
        this.organizations = organizations;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public OrganizationId execute(Command command) {
        Objects.requireNonNull(command, "command");
        LocalDate establishedOn =
                command.establishedOn() == null || command.establishedOn().isBlank()
                        ? null
                        : LocalDate.parse(command.establishedOn());
        Organization organization = Organization.register(
                LegalName.of(command.legalName()),
                command.tradeName(),
                OrganizationType.valueOf(command.type()),
                address(command),
                EthiopianPhoneNumber.parse(command.phone()),
                command.email(),
                TenantId.of(command.registeringTenantId()),
                command.ownerCitizenId(),
                establishedOn,
                LocalDate.now(EthGovClocks.ADDIS_ABABA),
                clock.instant());
        organizations.save(organization);
        eventPublisher.publishAll(organization.pullDomainEvents());
        return organization.id();
    }

    private static EthiopianAddress address(Command command) {
        return EthiopianAddress.builder()
                .regionalState(RegionalState.fromCode(command.regionCode()))
                .zone(command.zoneCode(), command.zoneName())
                .woreda(command.woredaCode(), command.woredaName())
                .kebele(command.kebeleCode(), command.kebeleName())
                .locality(command.locality())
                .street(command.street())
                .houseNumber(command.houseNumber())
                .build();
    }

    public record Command(
            String legalName,
            String tradeName,
            String type,
            String phone,
            String email,
            String registeringTenantId,
            String ownerCitizenId,
            String establishedOn,
            String regionCode,
            String zoneCode,
            String zoneName,
            String woredaCode,
            String woredaName,
            String kebeleCode,
            String kebeleName,
            String locality,
            String street,
            String houseNumber) {}
}
