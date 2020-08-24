package et.gov.ethgov.document.application.scanbatch;

import et.gov.ethgov.document.domain.scanbatch.Scanbatch;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchRepository;
import java.util.List;
import java.util.Objects;

public final class ListScanbatchUseCase {
    private final ScanbatchRepository repository;

    public ListScanbatchUseCase(ScanbatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Scanbatch> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
