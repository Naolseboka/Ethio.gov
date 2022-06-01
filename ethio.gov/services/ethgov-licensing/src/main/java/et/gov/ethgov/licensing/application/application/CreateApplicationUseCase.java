package et.gov.ethgov.licensing.application.application;

import et.gov.ethgov.licensing.domain.application.Application;
import et.gov.ethgov.licensing.domain.application.ApplicationId;
import et.gov.ethgov.licensing.domain.application.ApplicationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateApplicationUseCase {
    private final ApplicationRepository repository;
    private final Clock clock;

    public CreateApplicationUseCase(ApplicationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApplicationId execute(CreateApplicationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Application aggregate = Application.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateApplicationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String applicationId,
            String reason) {}
}
