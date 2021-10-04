package et.gov.ethgov.identity.application.oauthclient;

import et.gov.ethgov.identity.domain.oauthclient.Oauthclient;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientId;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientRepository;
import java.util.Objects;

public final class GetOauthclientUseCase {
    private final OauthclientRepository repository;

    public GetOauthclientUseCase(OauthclientRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Oauthclient execute(String rawId) {
        OauthclientId id = OauthclientId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("oauthclient not found: " + rawId));
    }
}
