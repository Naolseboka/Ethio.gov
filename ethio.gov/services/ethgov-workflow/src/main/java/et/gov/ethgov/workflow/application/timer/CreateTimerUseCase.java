package et.gov.ethgov.workflow.application.timer;

import et.gov.ethgov.workflow.domain.timer.Timer;
import et.gov.ethgov.workflow.domain.timer.TimerId;
import et.gov.ethgov.workflow.domain.timer.TimerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTimerUseCase {
    private final TimerRepository repository;
    private final Clock clock;

    public CreateTimerUseCase(TimerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TimerId execute(CreateTimerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Timer aggregate = Timer.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTimerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String timerId,
            String reason) {}
}
