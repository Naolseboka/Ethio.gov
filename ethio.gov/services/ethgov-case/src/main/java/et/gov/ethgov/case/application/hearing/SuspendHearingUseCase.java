package et.gov.ethgov.case.application.hearing;

import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingId;
import et.gov.ethgov.case.domain.hearing.HearingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendHearingUseCase {
    private final HearingRepository repository;
    private final Clock clock;

    public SuspendHearingUseCase(HearingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HearingId execute(SuspendHearingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HearingId id = HearingId.parse(command.hearingId());
        Hearing aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("hearing not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendHearingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String hearingId,
            String reason) {}
}
