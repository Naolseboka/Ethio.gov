package et.gov.ethgov.directory.application.publiclisting;

import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingId;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingRepository;
import java.util.Objects;

public final class GetPubliclistingUseCase {
    private final PubliclistingRepository repository;

    public GetPubliclistingUseCase(PubliclistingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Publiclisting execute(String rawId) {
        PubliclistingId id = PubliclistingId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("publiclisting not found: " + rawId));
    }
}
