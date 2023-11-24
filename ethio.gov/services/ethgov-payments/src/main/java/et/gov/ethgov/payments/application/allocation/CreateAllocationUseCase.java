package et.gov.ethgov.payments.application.allocation;

import et.gov.ethgov.payments.domain.allocation.Allocation;
import et.gov.ethgov.payments.domain.allocation.AllocationId;
import et.gov.ethgov.payments.domain.allocation.AllocationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAllocationUseCase {
    private final AllocationRepository repository;
    private final Clock clock;

    public CreateAllocationUseCase(AllocationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AllocationId execute(CreateAllocationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Allocation aggregate = Allocation.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAllocationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String allocationId,
            String reason) {}
}
