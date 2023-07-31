package et.gov.ethgov.organization.application.organization;

import et.gov.ethgov.organization.domain.organization.Organization;
import et.gov.ethgov.organization.domain.organization.OrganizationId;
import et.gov.ethgov.organization.domain.organization.OrganizationRepository;
import et.gov.ethgov.organization.domain.organization.TinNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignTinUseCase {

    private final OrganizationRepository organizations;
    private final OrganizationEventPublisher eventPublisher;
    private final Clock clock;

    public AssignTinUseCase(
            OrganizationRepository organizations, OrganizationEventPublisher eventPublisher, Clock clock) {
        this.organizations = organizations;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String organizationId, String tinRaw) {
        Objects.requireNonNull(organizationId, "organizationId");
        TinNumber tin = TinNumber.of(tinRaw);
        OrganizationId id = OrganizationId.of(organizationId);
        organizations.findByTin(tin).ifPresent(existing -> {
            if (!existing.id().equals(id)) {
                throw new DomainException(
                        "TIN_ALREADY_REGISTERED",
                        "TIN Already Registered",
                        "An organization with this TIN already exists",
                        409);
            }
        });
        Organization organization = organizations
                .findById(id)
                .orElseThrow(() -> new DomainException(
                        "ORGANIZATION_NOT_FOUND",
                        "Organization Not Found",
                        "Organization does not exist",
                        404));
        organization.assignTin(tin, clock.instant());
        organizations.save(organization);
        eventPublisher.publishAll(organization.pullDomainEvents());
    }
}
