package et.gov.ethgov.organization.application.servicecatalog;

import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogId;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateServicecatalogUseCase {
    private final ServicecatalogRepository repository;
    private final Clock clock;

    public CreateServicecatalogUseCase(ServicecatalogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ServicecatalogId execute(CreateServicecatalogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Servicecatalog aggregate = Servicecatalog.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateServicecatalogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String servicecatalogId,
            String reason) {}
}
