package et.gov.ethgov.directory.application.publiclisting;

import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingRepository;
import java.util.List;
import java.util.Objects;

public final class ListPubliclistingUseCase {
    private final PubliclistingRepository repository;

    public ListPubliclistingUseCase(PubliclistingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Publiclisting> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
