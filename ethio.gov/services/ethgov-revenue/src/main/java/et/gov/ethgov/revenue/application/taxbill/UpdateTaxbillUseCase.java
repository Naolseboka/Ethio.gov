package et.gov.ethgov.revenue.application.taxbill;

import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillId;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateTaxbillUseCase {
    private final TaxbillRepository repository;
    private final Clock clock;

    public UpdateTaxbillUseCase(TaxbillRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TaxbillId execute(UpdateTaxbillCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TaxbillId id = TaxbillId.parse(command.taxbillId());
        Taxbill aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("taxbill not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateTaxbillCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String taxbillId,
            String reason) {}
}
