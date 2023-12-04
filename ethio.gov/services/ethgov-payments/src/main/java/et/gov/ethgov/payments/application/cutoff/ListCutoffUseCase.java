package et.gov.ethgov.payments.application.cutoff;

import et.gov.ethgov.payments.domain.cutoff.Cutoff;
import et.gov.ethgov.payments.domain.cutoff.CutoffRepository;
import java.util.List;
import java.util.Objects;

public final class ListCutoffUseCase {
    private final CutoffRepository repository;

    public ListCutoffUseCase(CutoffRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Cutoff> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
