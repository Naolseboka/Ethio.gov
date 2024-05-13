package et.gov.ethgov.permits.application.temporary;

import et.gov.ethgov.permits.domain.temporary.Temporary;
import et.gov.ethgov.permits.domain.temporary.TemporaryRepository;
import java.util.List;
import java.util.Objects;

public final class ListTemporaryUseCase {
    private final TemporaryRepository repository;

    public ListTemporaryUseCase(TemporaryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Temporary> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
