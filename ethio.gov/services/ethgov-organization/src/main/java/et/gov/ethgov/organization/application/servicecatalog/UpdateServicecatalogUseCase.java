package et.gov.ethgov.organization.application.servicecatalog;

import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogId;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateServicecatalogUseCase {
    private final ServicecatalogRepository repository;
    private final Clock clock;

    public UpdateServicecatalogUseCase(ServicecatalogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ServicecatalogId execute(UpdateServicecatalogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ServicecatalogId id = ServicecatalogId.parse(command.servicecatalogId());
        Servicecatalog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("servicecatalog not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateServicecatalogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String servicecatalogId,
            String reason) {}
}
