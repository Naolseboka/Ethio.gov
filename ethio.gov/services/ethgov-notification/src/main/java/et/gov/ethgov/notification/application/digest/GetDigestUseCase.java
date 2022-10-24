package et.gov.ethgov.notification.application.digest;

import et.gov.ethgov.notification.domain.digest.Digest;
import et.gov.ethgov.notification.domain.digest.DigestId;
import et.gov.ethgov.notification.domain.digest.DigestRepository;
import java.util.Objects;

public final class GetDigestUseCase {
    private final DigestRepository repository;

    public GetDigestUseCase(DigestRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Digest execute(String rawId) {
        DigestId id = DigestId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("digest not found: " + rawId));
    }
}
