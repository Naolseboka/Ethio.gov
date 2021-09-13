package et.gov.ethgov.identity.application.apikey;

import et.gov.ethgov.identity.domain.apikey.Apikey;
import et.gov.ethgov.identity.domain.apikey.ApikeyId;
import et.gov.ethgov.identity.domain.apikey.ApikeyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateApikeyUseCase {
    private final ApikeyRepository repository;
    private final Clock clock;

    public CreateApikeyUseCase(ApikeyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApikeyId execute(CreateApikeyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Apikey aggregate = Apikey.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateApikeyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String apikeyId,
            String reason) {}
}
