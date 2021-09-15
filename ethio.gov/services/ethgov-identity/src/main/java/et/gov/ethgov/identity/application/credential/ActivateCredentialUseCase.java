package et.gov.ethgov.identity.application.credential;

import et.gov.ethgov.identity.domain.credential.Credential;
import et.gov.ethgov.identity.domain.credential.CredentialId;
import et.gov.ethgov.identity.domain.credential.CredentialRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCredentialUseCase {
    private final CredentialRepository repository;
    private final Clock clock;

    public ActivateCredentialUseCase(CredentialRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CredentialId execute(ActivateCredentialCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CredentialId id = CredentialId.parse(command.credentialId());
        Credential aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("credential not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCredentialCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String credentialId,
            String reason) {}
}
