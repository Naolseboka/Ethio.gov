package et.gov.ethgov.identity.application.credential;

import et.gov.ethgov.identity.domain.credential.Credential;
import et.gov.ethgov.identity.domain.credential.CredentialRepository;
import java.util.List;
import java.util.Objects;

public final class ListCredentialUseCase {
    private final CredentialRepository repository;

    public ListCredentialUseCase(CredentialRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Credential> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
