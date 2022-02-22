package et.gov.ethgov.land.application.title;

import et.gov.ethgov.land.domain.title.Title;
import et.gov.ethgov.land.domain.title.TitleId;
import et.gov.ethgov.land.domain.title.TitleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTitleUseCase {
    private final TitleRepository repository;
    private final Clock clock;

    public CreateTitleUseCase(TitleRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TitleId execute(CreateTitleCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Title aggregate = Title.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTitleCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String titleId,
            String reason) {}
}
