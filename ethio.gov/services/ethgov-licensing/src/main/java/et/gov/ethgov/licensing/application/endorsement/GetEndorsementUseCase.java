package et.gov.ethgov.licensing.application.endorsement;

import et.gov.ethgov.licensing.domain.endorsement.Endorsement;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementId;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementRepository;
import java.util.Objects;

public final class GetEndorsementUseCase {
    private final EndorsementRepository repository;

    public GetEndorsementUseCase(EndorsementRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Endorsement execute(String rawId) {
        EndorsementId id = EndorsementId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("endorsement not found: " + rawId));
    }
}
