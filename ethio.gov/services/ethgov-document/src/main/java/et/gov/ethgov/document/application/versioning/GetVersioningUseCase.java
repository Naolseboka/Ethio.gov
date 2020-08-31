package et.gov.ethgov.document.application.versioning;

import et.gov.ethgov.document.domain.versioning.Versioning;
import et.gov.ethgov.document.domain.versioning.VersioningId;
import et.gov.ethgov.document.domain.versioning.VersioningRepository;
import java.util.Objects;

public final class GetVersioningUseCase {
    private final VersioningRepository repository;

    public GetVersioningUseCase(VersioningRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Versioning execute(String rawId) {
        VersioningId id = VersioningId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("versioning not found: " + rawId));
    }
}
