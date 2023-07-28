package et.gov.ethgov.organization.application.orgaddress;

import et.gov.ethgov.organization.domain.orgaddress.Orgaddress;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressRepository;
import java.util.List;
import java.util.Objects;

public final class ListOrgaddressUseCase {
    private final OrgaddressRepository repository;

    public ListOrgaddressUseCase(OrgaddressRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Orgaddress> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
