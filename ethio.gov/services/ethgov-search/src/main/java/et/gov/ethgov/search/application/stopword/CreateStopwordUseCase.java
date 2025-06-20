package et.gov.ethgov.search.application.stopword;

import et.gov.ethgov.search.domain.stopword.Stopword;
import et.gov.ethgov.search.domain.stopword.StopwordId;
import et.gov.ethgov.search.domain.stopword.StopwordRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateStopwordUseCase {
    private final StopwordRepository repository;
    private final Clock clock;

    public CreateStopwordUseCase(StopwordRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public StopwordId execute(CreateStopwordCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Stopword aggregate = Stopword.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateStopwordCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String stopwordId,
            String reason) {}
}
