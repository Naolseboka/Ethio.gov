package et.gov.ethgov.revenue.application.revenueappeal;

import et.gov.ethgov.revenue.domain.revenueappeal.Revenueappeal;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealId;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealRepository;
import java.util.Objects;

public final class GetRevenueappealUseCase {
    private final RevenueappealRepository repository;

    public GetRevenueappealUseCase(RevenueappealRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Revenueappeal execute(String rawId) {
        RevenueappealId id = RevenueappealId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("revenueappeal not found: " + rawId));
    }
}
