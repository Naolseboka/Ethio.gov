package et.gov.ethgov.gateway.application.authn;

import et.gov.ethgov.gateway.domain.authn.Authn;
import et.gov.ethgov.gateway.domain.authn.AuthnId;
import et.gov.ethgov.gateway.domain.authn.AuthnRepository;
import java.util.Objects;

public final class GetAuthnUseCase {
    private final AuthnRepository repository;

    public GetAuthnUseCase(AuthnRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Authn execute(String rawId) {
        AuthnId id = AuthnId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("authn not found: " + rawId));
    }
}
