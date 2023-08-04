package et.gov.ethgov.organization.application.orgcontact;

import et.gov.ethgov.organization.domain.orgcontact.Orgcontact;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactId;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactRepository;
import java.util.Objects;

public final class GetOrgcontactUseCase {
    private final OrgcontactRepository repository;

    public GetOrgcontactUseCase(OrgcontactRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Orgcontact execute(String rawId) {
        OrgcontactId id = OrgcontactId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("orgcontact not found: " + rawId));
    }
}
