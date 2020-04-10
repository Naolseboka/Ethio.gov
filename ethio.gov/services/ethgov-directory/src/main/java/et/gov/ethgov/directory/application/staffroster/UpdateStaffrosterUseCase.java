package et.gov.ethgov.directory.application.staffroster;

import et.gov.ethgov.directory.domain.staffroster.Staffroster;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterId;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateStaffrosterUseCase {
    private final StaffrosterRepository repository;
    private final Clock clock;

    public UpdateStaffrosterUseCase(StaffrosterRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public StaffrosterId execute(UpdateStaffrosterCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        StaffrosterId id = StaffrosterId.parse(command.staffrosterId());
        Staffroster aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("staffroster not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateStaffrosterCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String staffrosterId,
            String reason) {}
}
