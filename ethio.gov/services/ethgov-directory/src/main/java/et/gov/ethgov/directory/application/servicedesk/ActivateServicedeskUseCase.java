package et.gov.ethgov.directory.application.servicedesk;

import et.gov.ethgov.directory.domain.servicedesk.Servicedesk;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskId;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateServicedeskUseCase {
    private final ServicedeskRepository repository;
    private final Clock clock;

    public ActivateServicedeskUseCase(ServicedeskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ServicedeskId execute(ActivateServicedeskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ServicedeskId id = ServicedeskId.parse(command.servicedeskId());
        Servicedesk aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("servicedesk not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateServicedeskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String servicedeskId,
            String reason) {}
}
