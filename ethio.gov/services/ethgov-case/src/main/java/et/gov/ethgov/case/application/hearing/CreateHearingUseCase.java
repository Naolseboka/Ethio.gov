package et.gov.ethgov.case.application.hearing;

import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingId;
import et.gov.ethgov.case.domain.hearing.HearingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateHearingUseCase {
    private final HearingRepository repository;
    private final Clock clock;

    public CreateHearingUseCase(HearingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HearingId execute(CreateHearingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Hearing aggregate = Hearing.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateHearingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String hearingId,
            String reason) {}
}
