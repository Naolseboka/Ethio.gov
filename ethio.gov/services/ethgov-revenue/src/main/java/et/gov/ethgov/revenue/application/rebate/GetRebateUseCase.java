package et.gov.ethgov.revenue.application.rebate;

import et.gov.ethgov.revenue.domain.rebate.Rebate;
import et.gov.ethgov.revenue.domain.rebate.RebateId;
import et.gov.ethgov.revenue.domain.rebate.RebateRepository;
import java.util.Objects;

public final class GetRebateUseCase {
    private final RebateRepository repository;

    public GetRebateUseCase(RebateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Rebate execute(String rawId) {
        RebateId id = RebateId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("rebate not found: " + rawId));
    }
}
