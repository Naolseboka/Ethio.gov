package et.gov.ethgov.gateway.application.cors;

import et.gov.ethgov.gateway.domain.cors.Cors;
import et.gov.ethgov.gateway.domain.cors.CorsRepository;
import java.util.List;
import java.util.Objects;

public final class ListCorsUseCase {
    private final CorsRepository repository;

    public ListCorsUseCase(CorsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Cors> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
