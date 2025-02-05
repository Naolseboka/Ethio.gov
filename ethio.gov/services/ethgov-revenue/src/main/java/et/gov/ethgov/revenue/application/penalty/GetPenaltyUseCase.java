package et.gov.ethgov.revenue.application.penalty;

import et.gov.ethgov.revenue.domain.penalty.Penalty;
import et.gov.ethgov.revenue.domain.penalty.PenaltyId;
import et.gov.ethgov.revenue.domain.penalty.PenaltyRepository;
import java.util.Objects;

public final class GetPenaltyUseCase {
    private final PenaltyRepository repository;

    public GetPenaltyUseCase(PenaltyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Penalty execute(String rawId) {
        PenaltyId id = PenaltyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("penalty not found: " + rawId));
    }
}
