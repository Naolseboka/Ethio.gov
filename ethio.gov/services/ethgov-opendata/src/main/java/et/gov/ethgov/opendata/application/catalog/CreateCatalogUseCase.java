package et.gov.ethgov.opendata.application.catalog;

import et.gov.ethgov.opendata.domain.catalog.Catalog;
import et.gov.ethgov.opendata.domain.catalog.CatalogId;
import et.gov.ethgov.opendata.domain.catalog.CatalogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCatalogUseCase {
    private final CatalogRepository repository;
    private final Clock clock;

    public CreateCatalogUseCase(CatalogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CatalogId execute(CreateCatalogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Catalog aggregate = Catalog.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCatalogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String catalogId,
            String reason) {}
}
