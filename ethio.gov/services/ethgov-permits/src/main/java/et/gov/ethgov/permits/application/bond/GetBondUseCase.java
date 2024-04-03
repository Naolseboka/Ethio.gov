package et.gov.ethgov.permits.application.bond;

import et.gov.ethgov.permits.domain.bond.Bond;
import et.gov.ethgov.permits.domain.bond.BondId;
import et.gov.ethgov.permits.domain.bond.BondRepository;
import java.util.Objects;

public final class GetBondUseCase {
    private final BondRepository repository;

    public GetBondUseCase(BondRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Bond execute(String rawId) {
        BondId id = BondId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("bond not found: " + rawId));
    }
}
