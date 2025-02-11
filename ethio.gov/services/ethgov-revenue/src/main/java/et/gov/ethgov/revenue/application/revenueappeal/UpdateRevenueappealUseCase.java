package et.gov.ethgov.revenue.application.revenueappeal;

import et.gov.ethgov.revenue.domain.revenueappeal.Revenueappeal;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealId;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRevenueappealUseCase {
    private final RevenueappealRepository repository;
    private final Clock clock;

    public UpdateRevenueappealUseCase(RevenueappealRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RevenueappealId execute(UpdateRevenueappealCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RevenueappealId id = RevenueappealId.parse(command.revenueappealId());
        Revenueappeal aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("revenueappeal not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRevenueappealCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String revenueappealId,
            String reason) {}
}
