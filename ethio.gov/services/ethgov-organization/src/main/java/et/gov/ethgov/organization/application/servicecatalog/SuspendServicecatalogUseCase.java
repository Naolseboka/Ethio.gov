package et.gov.ethgov.organization.application.servicecatalog;

import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogId;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendServicecatalogUseCase {
    private final ServicecatalogRepository repository;
    private final Clock clock;

    public SuspendServicecatalogUseCase(ServicecatalogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ServicecatalogId execute(SuspendServicecatalogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ServicecatalogId id = ServicecatalogId.parse(command.servicecatalogId());
        Servicecatalog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("servicecatalog not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendServicecatalogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String servicecatalogId,
            String reason) {}
}
