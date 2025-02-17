package et.gov.ethgov.revenue.application.writeoff;

import et.gov.ethgov.revenue.domain.writeoff.Writeoff;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffRepository;
import java.util.List;
import java.util.Objects;

public final class ListWriteoffUseCase {
    private final WriteoffRepository repository;

    public ListWriteoffUseCase(WriteoffRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Writeoff> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
