package et.gov.ethgov.organization.application.organization;

import et.gov.ethgov.organization.domain.organization.Organization;
import et.gov.ethgov.organization.domain.organization.OrganizationId;
import et.gov.ethgov.organization.domain.organization.OrganizationRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuspendOrganizationUseCase {

    private final OrganizationRepository organizations;
    private final OrganizationEventPublisher eventPublisher;
    private final Clock clock;

    public SuspendOrganizationUseCase(
            OrganizationRepository organizations, OrganizationEventPublisher eventPublisher, Clock clock) {
        this.organizations = organizations;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String organizationId, String reason) {
        Objects.requireNonNull(organizationId, "organizationId");
        Organization organization = require(organizationId);
        organization.suspend(reason, clock.instant());
        organizations.save(organization);
        eventPublisher.publishAll(organization.pullDomainEvents());
    }

    private Organization require(String organizationId) {
        return organizations
                .findById(OrganizationId.of(organizationId))
                .orElseThrow(() -> new DomainException(
                        "ORGANIZATION_NOT_FOUND",
                        "Organization Not Found",
                        "Organization does not exist",
                        404));
    }
}
