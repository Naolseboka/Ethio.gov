package et.gov.ethgov.case.application.hearing;

import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingId;
import et.gov.ethgov.case.domain.hearing.HearingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateHearingUseCase {
    private final HearingRepository repository;
    private final Clock clock;

    public ActivateHearingUseCase(HearingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HearingId execute(ActivateHearingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HearingId id = HearingId.parse(command.hearingId());
        Hearing aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("hearing not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateHearingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String hearingId,
            String reason) {}
}
