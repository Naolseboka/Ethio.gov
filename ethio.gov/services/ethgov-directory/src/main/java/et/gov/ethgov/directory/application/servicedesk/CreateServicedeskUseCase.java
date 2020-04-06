package et.gov.ethgov.directory.application.servicedesk;

import et.gov.ethgov.directory.domain.servicedesk.Servicedesk;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskId;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateServicedeskUseCase {
    private final ServicedeskRepository repository;
    private final Clock clock;

    public CreateServicedeskUseCase(ServicedeskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ServicedeskId execute(CreateServicedeskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Servicedesk aggregate = Servicedesk.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateServicedeskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String servicedeskId,
            String reason) {}
}
