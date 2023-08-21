package et.gov.ethgov.organization.application.servicecatalog;

import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogId;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateServicecatalogUseCase {
    private final ServicecatalogRepository repository;
    private final Clock clock;

    public ActivateServicecatalogUseCase(ServicecatalogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ServicecatalogId execute(ActivateServicecatalogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ServicecatalogId id = ServicecatalogId.parse(command.servicecatalogId());
        Servicecatalog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("servicecatalog not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateServicecatalogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String servicecatalogId,
            String reason) {}
}
