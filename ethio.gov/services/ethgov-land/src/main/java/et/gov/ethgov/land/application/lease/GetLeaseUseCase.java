package et.gov.ethgov.land.application.lease;

import et.gov.ethgov.land.domain.lease.Lease;
import et.gov.ethgov.land.domain.lease.LeaseId;
import et.gov.ethgov.land.domain.lease.LeaseRepository;
import java.util.Objects;

public final class GetLeaseUseCase {
    private final LeaseRepository repository;

    public GetLeaseUseCase(LeaseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Lease execute(String rawId) {
        LeaseId id = LeaseId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("lease not found: " + rawId));
    }
}
