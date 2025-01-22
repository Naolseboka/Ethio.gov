package et.gov.ethgov.revenue.application.exemption;

import et.gov.ethgov.revenue.domain.exemption.Exemption;
import et.gov.ethgov.revenue.domain.exemption.ExemptionRepository;
import java.util.List;
import java.util.Objects;

public final class ListExemptionUseCase {
    private final ExemptionRepository repository;

    public ListExemptionUseCase(ExemptionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Exemption> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
