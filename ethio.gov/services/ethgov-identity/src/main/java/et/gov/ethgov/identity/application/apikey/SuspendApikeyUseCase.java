package et.gov.ethgov.identity.application.apikey;

import et.gov.ethgov.identity.domain.apikey.Apikey;
import et.gov.ethgov.identity.domain.apikey.ApikeyId;
import et.gov.ethgov.identity.domain.apikey.ApikeyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendApikeyUseCase {
    private final ApikeyRepository repository;
    private final Clock clock;

    public SuspendApikeyUseCase(ApikeyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApikeyId execute(SuspendApikeyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ApikeyId id = ApikeyId.parse(command.apikeyId());
        Apikey aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("apikey not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendApikeyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String apikeyId,
            String reason) {}
}
