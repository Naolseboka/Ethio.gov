package et.gov.ethgov.identity.application.oauthclient;

import et.gov.ethgov.identity.domain.oauthclient.Oauthclient;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientId;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateOauthclientUseCase {
    private final OauthclientRepository repository;
    private final Clock clock;

    public ActivateOauthclientUseCase(OauthclientRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OauthclientId execute(ActivateOauthclientCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OauthclientId id = OauthclientId.parse(command.oauthclientId());
        Oauthclient aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("oauthclient not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateOauthclientCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String oauthclientId,
            String reason) {}
}
