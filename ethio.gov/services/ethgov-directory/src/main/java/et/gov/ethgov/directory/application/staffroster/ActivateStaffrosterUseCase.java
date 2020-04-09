package et.gov.ethgov.directory.application.staffroster;

import et.gov.ethgov.directory.domain.staffroster.Staffroster;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterId;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateStaffrosterUseCase {
    private final StaffrosterRepository repository;
    private final Clock clock;

    public ActivateStaffrosterUseCase(StaffrosterRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public StaffrosterId execute(ActivateStaffrosterCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        StaffrosterId id = StaffrosterId.parse(command.staffrosterId());
        Staffroster aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("staffroster not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateStaffrosterCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String staffrosterId,
            String reason) {}
}
