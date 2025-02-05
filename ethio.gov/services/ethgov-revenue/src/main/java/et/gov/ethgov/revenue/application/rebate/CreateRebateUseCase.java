package et.gov.ethgov.revenue.application.rebate;

import et.gov.ethgov.revenue.domain.rebate.Rebate;
import et.gov.ethgov.revenue.domain.rebate.RebateId;
import et.gov.ethgov.revenue.domain.rebate.RebateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRebateUseCase {
    private final RebateRepository repository;
    private final Clock clock;

    public CreateRebateUseCase(RebateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RebateId execute(CreateRebateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Rebate aggregate = Rebate.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRebateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rebateId,
            String reason) {}
}
