package et.gov.ethgov.citizen.application.addresshistory;

import et.gov.ethgov.citizen.domain.addresshistory.Addresshistory;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryId;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryRepository;
import java.util.Objects;

public final class GetAddresshistoryUseCase {
    private final AddresshistoryRepository repository;

    public GetAddresshistoryUseCase(AddresshistoryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Addresshistory execute(String rawId) {
        AddresshistoryId id = AddresshistoryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("addresshistory not found: " + rawId));
    }
}
