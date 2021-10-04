package et.gov.ethgov.identity.application.oauthclient;

import et.gov.ethgov.identity.domain.oauthclient.Oauthclient;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientRepository;
import java.util.List;
import java.util.Objects;

public final class ListOauthclientUseCase {
    private final OauthclientRepository repository;

    public ListOauthclientUseCase(OauthclientRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Oauthclient> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
