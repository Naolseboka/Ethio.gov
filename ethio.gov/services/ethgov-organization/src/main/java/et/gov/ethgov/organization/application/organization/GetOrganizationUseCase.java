package et.gov.ethgov.organization.application.organization;

import et.gov.ethgov.organization.domain.organization.Organization;
import et.gov.ethgov.organization.domain.organization.OrganizationId;
import et.gov.ethgov.organization.domain.organization.OrganizationRepository;
import et.gov.ethgov.organization.domain.organization.TinNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetOrganizationUseCase {

    private final OrganizationRepository organizations;

    public GetOrganizationUseCase(OrganizationRepository organizations) {
        this.organizations = organizations;
    }

    @Transactional(readOnly = true)
    public Organization byId(String organizationId) {
        return organizations
                .findById(OrganizationId.of(organizationId))
                .orElseThrow(() -> new DomainException(
                        "ORGANIZATION_NOT_FOUND",
                        "Organization Not Found",
                        "Organization does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public Optional<Organization> byTin(String tin) {
        return organizations.findByTin(TinNumber.of(tin));
    }
}
