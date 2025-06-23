package et.gov.ethgov.search.application.stopword;

import et.gov.ethgov.search.domain.stopword.Stopword;
import et.gov.ethgov.search.domain.stopword.StopwordId;
import et.gov.ethgov.search.domain.stopword.StopwordRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateStopwordUseCase {
    private final StopwordRepository repository;
    private final Clock clock;

    public UpdateStopwordUseCase(StopwordRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public StopwordId execute(UpdateStopwordCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        StopwordId id = StopwordId.parse(command.stopwordId());
        Stopword aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("stopword not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateStopwordCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String stopwordId,
            String reason) {}
}
