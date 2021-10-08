package et.gov.ethgov.identity.application.passwordpolicy;

import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyRepository;
import java.util.List;
import java.util.Objects;

public final class ListPasswordpolicyUseCase {
    private final PasswordpolicyRepository repository;

    public ListPasswordpolicyUseCase(PasswordpolicyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Passwordpolicy> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
