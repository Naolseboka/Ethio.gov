package et.gov.ethgov.directory.application.staffroster;

import et.gov.ethgov.directory.domain.staffroster.Staffroster;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterId;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateStaffrosterUseCase {
    private final StaffrosterRepository repository;
    private final Clock clock;

    public CreateStaffrosterUseCase(StaffrosterRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public StaffrosterId execute(CreateStaffrosterCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Staffroster aggregate = Staffroster.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateStaffrosterCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String staffrosterId,
            String reason) {}
}
