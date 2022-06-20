package et.gov.ethgov.licensing.application.holder;

import et.gov.ethgov.licensing.domain.holder.Holder;
import et.gov.ethgov.licensing.domain.holder.HolderRepository;
import java.util.List;
import java.util.Objects;

public final class ListHolderUseCase {
    private final HolderRepository repository;

    public ListHolderUseCase(HolderRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Holder> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
