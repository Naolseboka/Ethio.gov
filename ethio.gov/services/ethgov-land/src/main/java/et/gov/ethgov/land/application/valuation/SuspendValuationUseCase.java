package et.gov.ethgov.land.application.valuation;

import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationId;
import et.gov.ethgov.land.domain.valuation.ValuationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendValuationUseCase {
    private final ValuationRepository repository;
    private final Clock clock;

    public SuspendValuationUseCase(ValuationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ValuationId execute(SuspendValuationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ValuationId id = ValuationId.parse(command.valuationId());
        Valuation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("valuation not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendValuationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String valuationId,
            String reason) {}
}
