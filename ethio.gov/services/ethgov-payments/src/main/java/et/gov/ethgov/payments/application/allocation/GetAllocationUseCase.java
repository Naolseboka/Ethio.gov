package et.gov.ethgov.payments.application.allocation;

import et.gov.ethgov.payments.domain.allocation.Allocation;
import et.gov.ethgov.payments.domain.allocation.AllocationId;
import et.gov.ethgov.payments.domain.allocation.AllocationRepository;
import java.util.Objects;

public final class GetAllocationUseCase {
    private final AllocationRepository repository;

    public GetAllocationUseCase(AllocationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Allocation execute(String rawId) {
        AllocationId id = AllocationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("allocation not found: " + rawId));
    }
}
