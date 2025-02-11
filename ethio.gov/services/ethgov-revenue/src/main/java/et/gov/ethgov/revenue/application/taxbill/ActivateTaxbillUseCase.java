package et.gov.ethgov.revenue.application.taxbill;

import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillId;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTaxbillUseCase {
    private final TaxbillRepository repository;
    private final Clock clock;

    public ActivateTaxbillUseCase(TaxbillRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TaxbillId execute(ActivateTaxbillCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TaxbillId id = TaxbillId.parse(command.taxbillId());
        Taxbill aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("taxbill not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTaxbillCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String taxbillId,
            String reason) {}
}
