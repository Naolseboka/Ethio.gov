package et.gov.ethgov.developer.application.devratelimit;

import et.gov.ethgov.developer.domain.devratelimit.Devratelimit;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitRepository;
import java.util.List;
import java.util.Objects;

public final class ListDevratelimitUseCase {
    private final DevratelimitRepository repository;

    public ListDevratelimitUseCase(DevratelimitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Devratelimit> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
