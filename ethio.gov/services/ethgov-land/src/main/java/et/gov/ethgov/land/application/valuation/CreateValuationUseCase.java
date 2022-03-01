package et.gov.ethgov.land.application.valuation;

import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationId;
import et.gov.ethgov.land.domain.valuation.ValuationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateValuationUseCase {
    private final ValuationRepository repository;
    private final Clock clock;

    public CreateValuationUseCase(ValuationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ValuationId execute(CreateValuationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Valuation aggregate = Valuation.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateValuationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String valuationId,
            String reason) {}
}
