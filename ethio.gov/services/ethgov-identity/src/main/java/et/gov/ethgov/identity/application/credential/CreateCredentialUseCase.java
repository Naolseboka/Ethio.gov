package et.gov.ethgov.identity.application.credential;

import et.gov.ethgov.identity.domain.credential.Credential;
import et.gov.ethgov.identity.domain.credential.CredentialId;
import et.gov.ethgov.identity.domain.credential.CredentialRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCredentialUseCase {
    private final CredentialRepository repository;
    private final Clock clock;

    public CreateCredentialUseCase(CredentialRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CredentialId execute(CreateCredentialCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Credential aggregate = Credential.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCredentialCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String credentialId,
            String reason) {}
}
