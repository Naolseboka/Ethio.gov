package et.gov.ethgov.reporting.application.schedule;

import et.gov.ethgov.reporting.domain.schedule.Schedule;
import et.gov.ethgov.reporting.domain.schedule.ScheduleId;
import et.gov.ethgov.reporting.domain.schedule.ScheduleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateScheduleUseCase {
    private final ScheduleRepository repository;
    private final Clock clock;

    public CreateScheduleUseCase(ScheduleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScheduleId execute(CreateScheduleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Schedule aggregate = Schedule.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateScheduleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scheduleId,
            String reason) {}
}
