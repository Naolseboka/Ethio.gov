package et.gov.ethgov.revenue.application.arrears;

import et.gov.ethgov.revenue.domain.arrears.Arrears;
import et.gov.ethgov.revenue.domain.arrears.ArrearsRepository;
import java.util.List;
import java.util.Objects;

public final class ListArrearsUseCase {
    private final ArrearsRepository repository;

    public ListArrearsUseCase(ArrearsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Arrears> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
