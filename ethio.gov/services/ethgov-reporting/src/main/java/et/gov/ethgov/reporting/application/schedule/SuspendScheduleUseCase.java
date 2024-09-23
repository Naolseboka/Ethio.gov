package et.gov.ethgov.reporting.application.schedule;

import et.gov.ethgov.reporting.domain.schedule.Schedule;
import et.gov.ethgov.reporting.domain.schedule.ScheduleId;
import et.gov.ethgov.reporting.domain.schedule.ScheduleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendScheduleUseCase {
    private final ScheduleRepository repository;
    private final Clock clock;

    public SuspendScheduleUseCase(ScheduleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScheduleId execute(SuspendScheduleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ScheduleId id = ScheduleId.parse(command.scheduleId());
        Schedule aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("schedule not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendScheduleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scheduleId,
            String reason) {}
}
