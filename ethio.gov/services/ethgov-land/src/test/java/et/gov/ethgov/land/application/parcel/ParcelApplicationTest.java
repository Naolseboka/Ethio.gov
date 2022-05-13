package et.gov.ethgov.land.application.parcel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryParcelRepository;
import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParcelApplicationTest {
    private InMemoryParcelRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryParcelRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateParcelUseCase(repository, clock);
        var activate = new ActivateParcelUseCase(repository, clock);
        var get = new GetParcelUseCase(repository);

        var id = create.execute(new CreateParcelUseCase.CreateParcelCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateParcelUseCase.ActivateParcelCommand(
                null, null, null, null, id.toString(), null));

        Parcel loaded = get.execute(id.toString());
        assertEquals(ParcelStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
