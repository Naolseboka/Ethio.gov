package et.gov.ethgov.permits.application.temporary;

import et.gov.ethgov.permits.domain.temporary.Temporary;
import et.gov.ethgov.permits.domain.temporary.TemporaryId;
import et.gov.ethgov.permits.domain.temporary.TemporaryRepository;
import java.util.Objects;

public final class GetTemporaryUseCase {
    private final TemporaryRepository repository;

    public GetTemporaryUseCase(TemporaryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Temporary execute(String rawId) {
        TemporaryId id = TemporaryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("temporary not found: " + rawId));
    }
}
