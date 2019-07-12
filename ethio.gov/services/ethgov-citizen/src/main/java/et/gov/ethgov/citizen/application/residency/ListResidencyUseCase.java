package et.gov.ethgov.citizen.application.residency;

import et.gov.ethgov.citizen.domain.residency.Residency;
import et.gov.ethgov.citizen.domain.residency.ResidencyRepository;
import java.util.List;
import java.util.Objects;

public final class ListResidencyUseCase {
    private final ResidencyRepository repository;

    public ListResidencyUseCase(ResidencyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Residency> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
