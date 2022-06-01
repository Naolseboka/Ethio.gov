package et.gov.ethgov.licensing.application.application;

import et.gov.ethgov.licensing.domain.application.Application;
import et.gov.ethgov.licensing.domain.application.ApplicationId;
import et.gov.ethgov.licensing.domain.application.ApplicationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateApplicationUseCase {
    private final ApplicationRepository repository;
    private final Clock clock;

    public ActivateApplicationUseCase(ApplicationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApplicationId execute(ActivateApplicationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ApplicationId id = ApplicationId.parse(command.applicationId());
        Application aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("application not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateApplicationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String applicationId,
            String reason) {}
}
