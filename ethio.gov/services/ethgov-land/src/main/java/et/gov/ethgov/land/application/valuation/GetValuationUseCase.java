package et.gov.ethgov.land.application.valuation;

import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationId;
import et.gov.ethgov.land.domain.valuation.ValuationRepository;
import java.util.Objects;

public final class GetValuationUseCase {
    private final ValuationRepository repository;

    public GetValuationUseCase(ValuationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Valuation execute(String rawId) {
        ValuationId id = ValuationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("valuation not found: " + rawId));
    }
}
