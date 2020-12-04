package et.gov.ethgov.gateway.application.authn;

import et.gov.ethgov.gateway.domain.authn.Authn;
import et.gov.ethgov.gateway.domain.authn.AuthnId;
import et.gov.ethgov.gateway.domain.authn.AuthnRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateAuthnUseCase {
    private final AuthnRepository repository;
    private final Clock clock;

    public ActivateAuthnUseCase(AuthnRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AuthnId execute(ActivateAuthnCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AuthnId id = AuthnId.parse(command.authnId());
        Authn aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("authn not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateAuthnCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String authnId,
            String reason) {}
}
