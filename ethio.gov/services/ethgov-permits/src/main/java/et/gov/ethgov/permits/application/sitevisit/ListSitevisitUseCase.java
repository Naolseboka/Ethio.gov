package et.gov.ethgov.permits.application.sitevisit;

import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitRepository;
import java.util.List;
import java.util.Objects;

public final class ListSitevisitUseCase {
    private final SitevisitRepository repository;

    public ListSitevisitUseCase(SitevisitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Sitevisit> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
