package et.gov.ethgov.land.application.valuation;

import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationId;
import et.gov.ethgov.land.domain.valuation.ValuationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateValuationUseCase {
    private final ValuationRepository repository;
    private final Clock clock;

    public ActivateValuationUseCase(ValuationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ValuationId execute(ActivateValuationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ValuationId id = ValuationId.parse(command.valuationId());
        Valuation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("valuation not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateValuationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String valuationId,
            String reason) {}
}
