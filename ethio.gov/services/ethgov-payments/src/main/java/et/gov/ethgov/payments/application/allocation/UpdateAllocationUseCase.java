package et.gov.ethgov.payments.application.allocation;

import et.gov.ethgov.payments.domain.allocation.Allocation;
import et.gov.ethgov.payments.domain.allocation.AllocationId;
import et.gov.ethgov.payments.domain.allocation.AllocationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAllocationUseCase {
    private final AllocationRepository repository;
    private final Clock clock;

    public UpdateAllocationUseCase(AllocationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AllocationId execute(UpdateAllocationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AllocationId id = AllocationId.parse(command.allocationId());
        Allocation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("allocation not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAllocationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String allocationId,
            String reason) {}
}
