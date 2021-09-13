package et.gov.ethgov.identity.application.apikey;

import et.gov.ethgov.identity.domain.apikey.Apikey;
import et.gov.ethgov.identity.domain.apikey.ApikeyRepository;
import java.util.List;
import java.util.Objects;

public final class ListApikeyUseCase {
    private final ApikeyRepository repository;

    public ListApikeyUseCase(ApikeyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Apikey> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
