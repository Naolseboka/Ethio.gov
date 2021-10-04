package et.gov.ethgov.identity.application.oauthclient;

import et.gov.ethgov.identity.domain.oauthclient.Oauthclient;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientId;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateOauthclientUseCase {
    private final OauthclientRepository repository;
    private final Clock clock;

    public UpdateOauthclientUseCase(OauthclientRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OauthclientId execute(UpdateOauthclientCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OauthclientId id = OauthclientId.parse(command.oauthclientId());
        Oauthclient aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("oauthclient not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateOauthclientCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String oauthclientId,
            String reason) {}
}
