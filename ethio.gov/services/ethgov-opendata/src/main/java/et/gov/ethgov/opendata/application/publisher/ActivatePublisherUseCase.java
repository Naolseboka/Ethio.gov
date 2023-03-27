package et.gov.ethgov.opendata.application.publisher;

import et.gov.ethgov.opendata.domain.publisher.Publisher;
import et.gov.ethgov.opendata.domain.publisher.PublisherId;
import et.gov.ethgov.opendata.domain.publisher.PublisherRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePublisherUseCase {
    private final PublisherRepository repository;
    private final Clock clock;

    public ActivatePublisherUseCase(PublisherRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PublisherId execute(ActivatePublisherCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PublisherId id = PublisherId.parse(command.publisherId());
        Publisher aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("publisher not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePublisherCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String publisherId,
            String reason) {}
}
