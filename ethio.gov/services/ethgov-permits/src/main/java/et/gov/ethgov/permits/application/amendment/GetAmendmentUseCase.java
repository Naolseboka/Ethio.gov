package et.gov.ethgov.permits.application.amendment;

import et.gov.ethgov.permits.domain.amendment.Amendment;
import et.gov.ethgov.permits.domain.amendment.AmendmentId;
import et.gov.ethgov.permits.domain.amendment.AmendmentRepository;
import java.util.Objects;

public final class GetAmendmentUseCase {
    private final AmendmentRepository repository;

    public GetAmendmentUseCase(AmendmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Amendment execute(String rawId) {
        AmendmentId id = AmendmentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("amendment not found: " + rawId));
    }
}
