package et.gov.ethgov.gateway.application.authn;

import et.gov.ethgov.gateway.domain.authn.Authn;
import et.gov.ethgov.gateway.domain.authn.AuthnId;
import et.gov.ethgov.gateway.domain.authn.AuthnRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAuthnUseCase {
    private final AuthnRepository repository;
    private final Clock clock;

    public CreateAuthnUseCase(AuthnRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AuthnId execute(CreateAuthnCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Authn aggregate = Authn.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAuthnCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String authnId,
            String reason) {}
}
