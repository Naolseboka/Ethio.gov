package et.gov.ethgov.organization.application.orgunit;

import et.gov.ethgov.organization.domain.orgunit.Orgunit;
import et.gov.ethgov.organization.domain.orgunit.OrgunitRepository;
import java.util.List;
import java.util.Objects;

public final class ListOrgunitUseCase {
    private final OrgunitRepository repository;

    public ListOrgunitUseCase(OrgunitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Orgunit> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
