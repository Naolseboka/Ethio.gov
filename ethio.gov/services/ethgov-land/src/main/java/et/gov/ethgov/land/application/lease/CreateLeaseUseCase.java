package et.gov.ethgov.land.application.lease;

import et.gov.ethgov.land.domain.lease.Lease;
import et.gov.ethgov.land.domain.lease.LeaseId;
import et.gov.ethgov.land.domain.lease.LeaseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateLeaseUseCase {
    private final LeaseRepository repository;
    private final Clock clock;

    public CreateLeaseUseCase(LeaseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LeaseId execute(CreateLeaseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Lease aggregate = Lease.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateLeaseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String leaseId,
            String reason) {}
}
