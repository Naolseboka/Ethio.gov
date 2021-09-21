package et.gov.ethgov.identity.application.delegation;

import et.gov.ethgov.identity.domain.delegation.Delegation;
import et.gov.ethgov.identity.domain.delegation.DelegationRepository;
import java.util.List;
import java.util.Objects;

public final class ListDelegationUseCase {
    private final DelegationRepository repository;

    public ListDelegationUseCase(DelegationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Delegation> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
