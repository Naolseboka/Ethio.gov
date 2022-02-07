package et.gov.ethgov.land.application.lease;

import et.gov.ethgov.land.domain.lease.Lease;
import et.gov.ethgov.land.domain.lease.LeaseId;
import et.gov.ethgov.land.domain.lease.LeaseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendLeaseUseCase {
    private final LeaseRepository repository;
    private final Clock clock;

    public SuspendLeaseUseCase(LeaseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LeaseId execute(SuspendLeaseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        LeaseId id = LeaseId.parse(command.leaseId());
        Lease aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("lease not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendLeaseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String leaseId,
            String reason) {}
}
