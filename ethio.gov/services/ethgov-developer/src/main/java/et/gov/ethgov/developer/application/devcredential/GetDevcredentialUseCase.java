package et.gov.ethgov.developer.application.devcredential;

import et.gov.ethgov.developer.domain.devcredential.Devcredential;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialId;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialRepository;
import java.util.Objects;

public final class GetDevcredentialUseCase {
    private final DevcredentialRepository repository;

    public GetDevcredentialUseCase(DevcredentialRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Devcredential execute(String rawId) {
        DevcredentialId id = DevcredentialId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devcredential not found: " + rawId));
    }
}
