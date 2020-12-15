package et.gov.ethgov.gateway.application.circuitbreaker;

import et.gov.ethgov.gateway.domain.circuitbreaker.Circuitbreaker;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerRepository;
import java.util.List;
import java.util.Objects;

public final class ListCircuitbreakerUseCase {
    private final CircuitbreakerRepository repository;

    public ListCircuitbreakerUseCase(CircuitbreakerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Circuitbreaker> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
