package et.gov.ethgov.payments.application.allocation;

import et.gov.ethgov.payments.domain.allocation.Allocation;
import et.gov.ethgov.payments.domain.allocation.AllocationRepository;
import java.util.List;
import java.util.Objects;

public final class ListAllocationUseCase {
    private final AllocationRepository repository;

    public ListAllocationUseCase(AllocationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Allocation> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
