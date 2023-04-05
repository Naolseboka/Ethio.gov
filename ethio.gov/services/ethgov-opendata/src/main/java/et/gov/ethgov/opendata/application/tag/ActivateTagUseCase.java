package et.gov.ethgov.opendata.application.tag;

import et.gov.ethgov.opendata.domain.tag.Tag;
import et.gov.ethgov.opendata.domain.tag.TagId;
import et.gov.ethgov.opendata.domain.tag.TagRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTagUseCase {
    private final TagRepository repository;
    private final Clock clock;

    public ActivateTagUseCase(TagRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TagId execute(ActivateTagCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TagId id = TagId.parse(command.tagId());
        Tag aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("tag not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTagCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String tagId,
            String reason) {}
}
