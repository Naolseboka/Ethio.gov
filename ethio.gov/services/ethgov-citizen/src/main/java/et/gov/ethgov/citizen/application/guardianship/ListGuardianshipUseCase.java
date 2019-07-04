package et.gov.ethgov.citizen.application.guardianship;

import et.gov.ethgov.citizen.domain.guardianship.Guardianship;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipRepository;
import java.util.List;
import java.util.Objects;

public final class ListGuardianshipUseCase {
    private final GuardianshipRepository repository;

    public ListGuardianshipUseCase(GuardianshipRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Guardianship> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
