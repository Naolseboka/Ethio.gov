package et.gov.ethgov.identity.application.oauthclient;

import et.gov.ethgov.identity.domain.oauthclient.Oauthclient;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientId;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOauthclientUseCase {
    private final OauthclientRepository repository;
    private final Clock clock;

    public CreateOauthclientUseCase(OauthclientRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OauthclientId execute(CreateOauthclientCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Oauthclient aggregate = Oauthclient.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOauthclientCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String oauthclientId,
            String reason) {}
}
