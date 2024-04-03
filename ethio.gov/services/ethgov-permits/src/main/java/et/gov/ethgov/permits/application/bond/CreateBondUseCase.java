package et.gov.ethgov.permits.application.bond;

import et.gov.ethgov.permits.domain.bond.Bond;
import et.gov.ethgov.permits.domain.bond.BondId;
import et.gov.ethgov.permits.domain.bond.BondRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateBondUseCase {
    private final BondRepository repository;
    private final Clock clock;

    public CreateBondUseCase(BondRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BondId execute(CreateBondCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Bond aggregate = Bond.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateBondCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String bondId,
            String reason) {}
}
