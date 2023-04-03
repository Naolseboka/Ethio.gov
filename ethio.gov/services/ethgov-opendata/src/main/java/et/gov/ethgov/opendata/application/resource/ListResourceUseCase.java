package et.gov.ethgov.opendata.application.resource;

import et.gov.ethgov.opendata.domain.resource.Resource;
import et.gov.ethgov.opendata.domain.resource.ResourceRepository;
import java.util.List;
import java.util.Objects;

public final class ListResourceUseCase {
    private final ResourceRepository repository;

    public ListResourceUseCase(ResourceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Resource> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
