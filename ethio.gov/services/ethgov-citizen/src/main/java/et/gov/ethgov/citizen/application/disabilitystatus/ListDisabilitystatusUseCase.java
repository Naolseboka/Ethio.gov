package et.gov.ethgov.citizen.application.disabilitystatus;

import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusRepository;
import java.util.List;
import java.util.Objects;

public final class ListDisabilitystatusUseCase {
    private final DisabilitystatusRepository repository;

    public ListDisabilitystatusUseCase(DisabilitystatusRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Disabilitystatus> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
