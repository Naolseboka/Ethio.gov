package et.gov.ethgov.search.application.crawljob;

import et.gov.ethgov.search.domain.crawljob.Crawljob;
import et.gov.ethgov.search.domain.crawljob.CrawljobId;
import et.gov.ethgov.search.domain.crawljob.CrawljobRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCrawljobUseCase {
    private final CrawljobRepository repository;
    private final Clock clock;

    public CreateCrawljobUseCase(CrawljobRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CrawljobId execute(CreateCrawljobCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Crawljob aggregate = Crawljob.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCrawljobCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String crawljobId,
            String reason) {}
}
