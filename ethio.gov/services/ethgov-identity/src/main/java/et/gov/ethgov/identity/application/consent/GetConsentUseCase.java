package et.gov.ethgov.identity.application.consent;

import et.gov.ethgov.identity.domain.consent.Consent;
import et.gov.ethgov.identity.domain.consent.ConsentId;
import et.gov.ethgov.identity.domain.consent.ConsentRepository;
import java.util.Objects;

public final class GetConsentUseCase {
    private final ConsentRepository repository;

    public GetConsentUseCase(ConsentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Consent execute(String rawId) {
        ConsentId id = ConsentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("consent not found: " + rawId));
    }
}
