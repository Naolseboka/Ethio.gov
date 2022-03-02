package et.gov.ethgov.land.application.valuation;

import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationId;
import et.gov.ethgov.land.domain.valuation.ValuationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateValuationUseCase {
    private final ValuationRepository repository;
    private final Clock clock;

    public UpdateValuationUseCase(ValuationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ValuationId execute(UpdateValuationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ValuationId id = ValuationId.parse(command.valuationId());
        Valuation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("valuation not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateValuationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String valuationId,
            String reason) {}
}
