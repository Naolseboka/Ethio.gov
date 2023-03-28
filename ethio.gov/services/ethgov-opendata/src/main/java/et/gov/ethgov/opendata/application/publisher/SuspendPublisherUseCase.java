package et.gov.ethgov.opendata.application.publisher;

import et.gov.ethgov.opendata.domain.publisher.Publisher;
import et.gov.ethgov.opendata.domain.publisher.PublisherId;
import et.gov.ethgov.opendata.domain.publisher.PublisherRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPublisherUseCase {
    private final PublisherRepository repository;
    private final Clock clock;

    public SuspendPublisherUseCase(PublisherRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PublisherId execute(SuspendPublisherCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PublisherId id = PublisherId.parse(command.publisherId());
        Publisher aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("publisher not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPublisherCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String publisherId,
            String reason) {}
}
