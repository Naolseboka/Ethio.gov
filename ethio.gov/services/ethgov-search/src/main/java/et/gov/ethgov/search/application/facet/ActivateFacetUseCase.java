package et.gov.ethgov.search.application.facet;

import et.gov.ethgov.search.domain.facet.Facet;
import et.gov.ethgov.search.domain.facet.FacetId;
import et.gov.ethgov.search.domain.facet.FacetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateFacetUseCase {
    private final FacetRepository repository;
    private final Clock clock;

    public ActivateFacetUseCase(FacetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FacetId execute(ActivateFacetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        FacetId id = FacetId.parse(command.facetId());
        Facet aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("facet not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateFacetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String facetId,
            String reason) {}
}
