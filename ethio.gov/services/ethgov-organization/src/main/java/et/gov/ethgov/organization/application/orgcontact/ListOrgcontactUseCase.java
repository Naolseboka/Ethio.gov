package et.gov.ethgov.organization.application.orgcontact;

import et.gov.ethgov.organization.domain.orgcontact.Orgcontact;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactRepository;
import java.util.List;
import java.util.Objects;

public final class ListOrgcontactUseCase {
    private final OrgcontactRepository repository;

    public ListOrgcontactUseCase(OrgcontactRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Orgcontact> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
