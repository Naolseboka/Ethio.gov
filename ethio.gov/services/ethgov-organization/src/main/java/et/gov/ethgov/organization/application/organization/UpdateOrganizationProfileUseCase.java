package et.gov.ethgov.organization.application.organization;

import et.gov.ethgov.organization.domain.organization.LegalName;
import et.gov.ethgov.organization.domain.organization.Organization;
import et.gov.ethgov.organization.domain.organization.OrganizationId;
import et.gov.ethgov.organization.domain.organization.OrganizationRepository;
import et.gov.ethgov.platform.commons.address.EthiopianAddress;
import et.gov.ethgov.platform.commons.address.RegionalState;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateOrganizationProfileUseCase {

    private final OrganizationRepository organizations;
    private final OrganizationEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateOrganizationProfileUseCase(
            OrganizationRepository organizations, OrganizationEventPublisher eventPublisher, Clock clock) {
        this.organizations = organizations;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        Organization organization = organizations
                .findById(OrganizationId.of(command.organizationId()))
                .orElseThrow(() -> new DomainException(
                        "ORGANIZATION_NOT_FOUND",
                        "Organization Not Found",
                        "Organization does not exist",
                        404));
        LocalDate establishedOn =
                command.establishedOn() == null || command.establishedOn().isBlank()
                        ? null
                        : LocalDate.parse(command.establishedOn());
        organization.updateProfile(
                LegalName.of(command.legalName()),
                command.tradeName(),
                EthiopianAddress.builder()
                        .regionalState(RegionalState.fromCode(command.regionCode()))
                        .zone(command.zoneCode(), command.zoneName())
                        .woreda(command.woredaCode(), command.woredaName())
                        .kebele(command.kebeleCode(), command.kebeleName())
                        .locality(command.locality())
                        .street(command.street())
                        .houseNumber(command.houseNumber())
                        .build(),
                EthiopianPhoneNumber.parse(command.phone()),
                command.email(),
                command.registrationNumber(),
                establishedOn,
                LocalDate.now(EthGovClocks.ADDIS_ABABA),
                clock.instant());
        organizations.save(organization);
        eventPublisher.publishAll(organization.pullDomainEvents());
    }

    public record Command(
            String organizationId,
            String legalName,
            String tradeName,
            String phone,
            String email,
            String registrationNumber,
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
