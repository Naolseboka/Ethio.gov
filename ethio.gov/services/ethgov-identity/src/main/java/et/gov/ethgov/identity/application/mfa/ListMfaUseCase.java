package et.gov.ethgov.identity.application.mfa;

import et.gov.ethgov.identity.domain.mfa.Mfa;
import et.gov.ethgov.identity.domain.mfa.MfaRepository;
import java.util.List;
import java.util.Objects;

public final class ListMfaUseCase {
    private final MfaRepository repository;

    public ListMfaUseCase(MfaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Mfa> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
