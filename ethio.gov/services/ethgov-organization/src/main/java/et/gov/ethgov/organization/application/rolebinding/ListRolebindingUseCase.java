package et.gov.ethgov.organization.application.rolebinding;

import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingRepository;
import java.util.List;
import java.util.Objects;

public final class ListRolebindingUseCase {
    private final RolebindingRepository repository;

    public ListRolebindingUseCase(RolebindingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Rolebinding> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
