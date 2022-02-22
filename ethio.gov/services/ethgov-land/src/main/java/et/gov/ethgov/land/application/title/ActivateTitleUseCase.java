package et.gov.ethgov.land.application.title;

import et.gov.ethgov.land.domain.title.Title;
import et.gov.ethgov.land.domain.title.TitleId;
import et.gov.ethgov.land.domain.title.TitleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTitleUseCase {
    private final TitleRepository repository;
    private final Clock clock;

    public ActivateTitleUseCase(TitleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TitleId execute(ActivateTitleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TitleId id = TitleId.parse(command.titleId());
        Title aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("title not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTitleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String titleId,
            String reason) {}
}
