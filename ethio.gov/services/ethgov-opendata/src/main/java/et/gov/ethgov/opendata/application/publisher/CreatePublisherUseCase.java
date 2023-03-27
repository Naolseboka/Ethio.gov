package et.gov.ethgov.opendata.application.publisher;

import et.gov.ethgov.opendata.domain.publisher.Publisher;
import et.gov.ethgov.opendata.domain.publisher.PublisherId;
import et.gov.ethgov.opendata.domain.publisher.PublisherRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePublisherUseCase {
    private final PublisherRepository repository;
    private final Clock clock;

    public CreatePublisherUseCase(PublisherRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PublisherId execute(CreatePublisherCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Publisher aggregate = Publisher.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePublisherCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String publisherId,
            String reason) {}
}
