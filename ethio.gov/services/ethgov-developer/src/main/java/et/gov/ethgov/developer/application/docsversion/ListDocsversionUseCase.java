package et.gov.ethgov.developer.application.docsversion;

import et.gov.ethgov.developer.domain.docsversion.Docsversion;
import et.gov.ethgov.developer.domain.docsversion.DocsversionRepository;
import java.util.List;
import java.util.Objects;

public final class ListDocsversionUseCase {
    private final DocsversionRepository repository;

    public ListDocsversionUseCase(DocsversionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Docsversion> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
