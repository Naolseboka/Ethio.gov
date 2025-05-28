package et.gov.ethgov.search.application.crawljob;

import et.gov.ethgov.search.domain.crawljob.Crawljob;
import et.gov.ethgov.search.domain.crawljob.CrawljobRepository;
import java.util.List;
import java.util.Objects;

public final class ListCrawljobUseCase {
    private final CrawljobRepository repository;

    public ListCrawljobUseCase(CrawljobRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Crawljob> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
