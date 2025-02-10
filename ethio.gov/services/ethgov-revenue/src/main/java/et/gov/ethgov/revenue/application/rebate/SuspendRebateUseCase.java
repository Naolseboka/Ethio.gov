package et.gov.ethgov.revenue.application.rebate;

import et.gov.ethgov.revenue.domain.rebate.Rebate;
import et.gov.ethgov.revenue.domain.rebate.RebateId;
import et.gov.ethgov.revenue.domain.rebate.RebateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendRebateUseCase {
    private final RebateRepository repository;
    private final Clock clock;

    public SuspendRebateUseCase(RebateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RebateId execute(SuspendRebateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RebateId id = RebateId.parse(command.rebateId());
        Rebate aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("rebate not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendRebateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rebateId,
            String reason) {}
}
