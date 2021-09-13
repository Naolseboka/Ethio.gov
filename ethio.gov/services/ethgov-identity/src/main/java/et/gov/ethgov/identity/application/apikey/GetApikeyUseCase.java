package et.gov.ethgov.identity.application.apikey;

import et.gov.ethgov.identity.domain.apikey.Apikey;
import et.gov.ethgov.identity.domain.apikey.ApikeyId;
import et.gov.ethgov.identity.domain.apikey.ApikeyRepository;
import java.util.Objects;

public final class GetApikeyUseCase {
    private final ApikeyRepository repository;

    public GetApikeyUseCase(ApikeyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Apikey execute(String rawId) {
        ApikeyId id = ApikeyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("apikey not found: " + rawId));
    }
}
