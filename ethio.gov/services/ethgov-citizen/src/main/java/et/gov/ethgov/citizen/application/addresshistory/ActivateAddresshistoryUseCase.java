package et.gov.ethgov.citizen.application.addresshistory;

import et.gov.ethgov.citizen.domain.addresshistory.Addresshistory;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryId;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateAddresshistoryUseCase {
    private final AddresshistoryRepository repository;
    private final Clock clock;

    public ActivateAddresshistoryUseCase(AddresshistoryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AddresshistoryId execute(ActivateAddresshistoryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AddresshistoryId id = AddresshistoryId.parse(command.addresshistoryId());
        Addresshistory aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("addresshistory not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateAddresshistoryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String addresshistoryId,
            String reason) {}
}
