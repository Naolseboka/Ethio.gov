package et.gov.ethgov.search.application.crawljob;

import et.gov.ethgov.search.domain.crawljob.Crawljob;
import et.gov.ethgov.search.domain.crawljob.CrawljobId;
import et.gov.ethgov.search.domain.crawljob.CrawljobRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendCrawljobUseCase {
    private final CrawljobRepository repository;
    private final Clock clock;

    public SuspendCrawljobUseCase(CrawljobRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CrawljobId execute(SuspendCrawljobCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CrawljobId id = CrawljobId.parse(command.crawljobId());
        Crawljob aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("crawljob not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendCrawljobCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String crawljobId,
            String reason) {}
}
