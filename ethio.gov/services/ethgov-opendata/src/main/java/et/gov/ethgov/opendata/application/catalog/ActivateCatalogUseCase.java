package et.gov.ethgov.opendata.application.catalog;

import et.gov.ethgov.opendata.domain.catalog.Catalog;
import et.gov.ethgov.opendata.domain.catalog.CatalogId;
import et.gov.ethgov.opendata.domain.catalog.CatalogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCatalogUseCase {
    private final CatalogRepository repository;
    private final Clock clock;

    public ActivateCatalogUseCase(CatalogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CatalogId execute(ActivateCatalogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CatalogId id = CatalogId.parse(command.catalogId());
        Catalog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("catalog not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCatalogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String catalogId,
            String reason) {}
}
