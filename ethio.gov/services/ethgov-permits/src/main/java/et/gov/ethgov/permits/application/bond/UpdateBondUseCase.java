package et.gov.ethgov.permits.application.bond;

import et.gov.ethgov.permits.domain.bond.Bond;
import et.gov.ethgov.permits.domain.bond.BondId;
import et.gov.ethgov.permits.domain.bond.BondRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateBondUseCase {
    private final BondRepository repository;
    private final Clock clock;

    public UpdateBondUseCase(BondRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BondId execute(UpdateBondCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BondId id = BondId.parse(command.bondId());
        Bond aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("bond not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateBondCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String bondId,
            String reason) {}
}
