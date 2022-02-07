package et.gov.ethgov.land.application.lease;

import et.gov.ethgov.land.domain.lease.Lease;
import et.gov.ethgov.land.domain.lease.LeaseRepository;
import java.util.List;
import java.util.Objects;

public final class ListLeaseUseCase {
    private final LeaseRepository repository;

    public ListLeaseUseCase(LeaseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Lease> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
