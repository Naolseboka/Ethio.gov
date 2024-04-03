package et.gov.ethgov.permits.application.bond;

import et.gov.ethgov.permits.domain.bond.Bond;
import et.gov.ethgov.permits.domain.bond.BondId;
import et.gov.ethgov.permits.domain.bond.BondRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateBondUseCase {
    private final BondRepository repository;
    private final Clock clock;

    public ActivateBondUseCase(BondRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BondId execute(ActivateBondCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BondId id = BondId.parse(command.bondId());
        Bond aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("bond not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateBondCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String bondId,
            String reason) {}
}
