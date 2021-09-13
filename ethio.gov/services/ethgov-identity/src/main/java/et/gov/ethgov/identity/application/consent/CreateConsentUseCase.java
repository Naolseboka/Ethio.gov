package et.gov.ethgov.identity.application.consent;

import et.gov.ethgov.identity.domain.consent.Consent;
import et.gov.ethgov.identity.domain.consent.ConsentId;
import et.gov.ethgov.identity.domain.consent.ConsentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateConsentUseCase {
    private final ConsentRepository repository;
    private final Clock clock;

    public CreateConsentUseCase(ConsentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ConsentId execute(CreateConsentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Consent aggregate = Consent.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateConsentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String consentId,
            String reason) {}
}
