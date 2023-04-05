package et.gov.ethgov.opendata.application.tag;

import et.gov.ethgov.opendata.domain.tag.Tag;
import et.gov.ethgov.opendata.domain.tag.TagId;
import et.gov.ethgov.opendata.domain.tag.TagRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTagUseCase {
    private final TagRepository repository;
    private final Clock clock;

    public CreateTagUseCase(TagRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TagId execute(CreateTagCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Tag aggregate = Tag.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTagCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String tagId,
            String reason) {}
}
