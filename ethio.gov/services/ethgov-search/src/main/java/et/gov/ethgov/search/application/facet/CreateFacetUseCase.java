package et.gov.ethgov.search.application.facet;

import et.gov.ethgov.search.domain.facet.Facet;
import et.gov.ethgov.search.domain.facet.FacetId;
import et.gov.ethgov.search.domain.facet.FacetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateFacetUseCase {
    private final FacetRepository repository;
    private final Clock clock;

    public CreateFacetUseCase(FacetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FacetId execute(CreateFacetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Facet aggregate = Facet.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateFacetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String facetId,
            String reason) {}
}
