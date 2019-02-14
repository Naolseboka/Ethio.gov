package et.gov.ethgov.case.application.hearing;

import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingId;
import et.gov.ethgov.case.domain.hearing.HearingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateHearingUseCase {
    private final HearingRepository repository;
    private final Clock clock;

    public UpdateHearingUseCase(HearingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HearingId execute(UpdateHearingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HearingId id = HearingId.parse(command.hearingId());
        Hearing aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("hearing not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateHearingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String hearingId,
            String reason) {}
}
