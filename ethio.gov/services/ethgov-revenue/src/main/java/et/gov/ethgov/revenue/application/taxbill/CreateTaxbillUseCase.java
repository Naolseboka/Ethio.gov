package et.gov.ethgov.revenue.application.taxbill;

import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillId;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTaxbillUseCase {
    private final TaxbillRepository repository;
    private final Clock clock;

    public CreateTaxbillUseCase(TaxbillRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TaxbillId execute(CreateTaxbillCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Taxbill aggregate = Taxbill.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTaxbillCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String taxbillId,
            String reason) {}
}
