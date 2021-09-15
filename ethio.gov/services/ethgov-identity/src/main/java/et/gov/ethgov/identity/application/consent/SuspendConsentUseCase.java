package et.gov.ethgov.identity.application.consent;

import et.gov.ethgov.identity.domain.consent.Consent;
import et.gov.ethgov.identity.domain.consent.ConsentId;
import et.gov.ethgov.identity.domain.consent.ConsentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendConsentUseCase {
    private final ConsentRepository repository;
    private final Clock clock;

    public SuspendConsentUseCase(ConsentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ConsentId execute(SuspendConsentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ConsentId id = ConsentId.parse(command.consentId());
        Consent aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("consent not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendConsentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String consentId,
            String reason) {}
}
