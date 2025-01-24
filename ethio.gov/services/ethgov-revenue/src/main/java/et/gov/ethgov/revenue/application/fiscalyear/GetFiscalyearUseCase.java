package et.gov.ethgov.revenue.application.fiscalyear;

import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearId;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearRepository;
import java.util.Objects;

public final class GetFiscalyearUseCase {
    private final FiscalyearRepository repository;

    public GetFiscalyearUseCase(FiscalyearRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Fiscalyear execute(String rawId) {
        FiscalyearId id = FiscalyearId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("fiscalyear not found: " + rawId));
    }
}
