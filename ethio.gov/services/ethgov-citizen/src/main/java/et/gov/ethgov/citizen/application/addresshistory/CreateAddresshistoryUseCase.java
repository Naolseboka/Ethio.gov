package et.gov.ethgov.citizen.application.addresshistory;

import et.gov.ethgov.citizen.domain.addresshistory.Addresshistory;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryId;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAddresshistoryUseCase {
    private final AddresshistoryRepository repository;
    private final Clock clock;

    public CreateAddresshistoryUseCase(AddresshistoryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AddresshistoryId execute(CreateAddresshistoryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Addresshistory aggregate = Addresshistory.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAddresshistoryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String addresshistoryId,
            String reason) {}
}
