package et.gov.ethgov.revenue.application.revenueappeal;

import et.gov.ethgov.revenue.domain.revenueappeal.Revenueappeal;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealId;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRevenueappealUseCase {
    private final RevenueappealRepository repository;
    private final Clock clock;

    public CreateRevenueappealUseCase(RevenueappealRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RevenueappealId execute(CreateRevenueappealCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Revenueappeal aggregate = Revenueappeal.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRevenueappealCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String revenueappealId,
            String reason) {}
}
