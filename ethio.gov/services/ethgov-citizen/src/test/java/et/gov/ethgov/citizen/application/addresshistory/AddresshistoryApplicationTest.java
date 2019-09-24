package et.gov.ethgov.citizen.application.addresshistory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryAddresshistoryRepository;
import et.gov.ethgov.citizen.domain.addresshistory.Addresshistory;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddresshistoryApplicationTest {
    private InMemoryAddresshistoryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAddresshistoryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAddresshistoryUseCase(repository, clock);
        var activate = new ActivateAddresshistoryUseCase(repository, clock);
        var get = new GetAddresshistoryUseCase(repository);

        var id = create.execute(new CreateAddresshistoryUseCase.CreateAddresshistoryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAddresshistoryUseCase.ActivateAddresshistoryCommand(
                null, null, null, null, id.toString(), null));

        Addresshistory loaded = get.execute(id.toString());
        assertEquals(AddresshistoryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
