package et.gov.ethgov.gateway.application.authn;

import et.gov.ethgov.gateway.domain.authn.Authn;
import et.gov.ethgov.gateway.domain.authn.AuthnRepository;
import java.util.List;
import java.util.Objects;

public final class ListAuthnUseCase {
    private final AuthnRepository repository;

    public ListAuthnUseCase(AuthnRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Authn> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
