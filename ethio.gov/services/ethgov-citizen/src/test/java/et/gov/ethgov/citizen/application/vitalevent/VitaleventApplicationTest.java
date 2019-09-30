package et.gov.ethgov.citizen.application.vitalevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryVitaleventRepository;
import et.gov.ethgov.citizen.domain.vitalevent.Vitalevent;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VitaleventApplicationTest {
    private InMemoryVitaleventRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVitaleventRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateVitaleventUseCase(repository, clock);
        var activate = new ActivateVitaleventUseCase(repository, clock);
        var get = new GetVitaleventUseCase(repository);

        var id = create.execute(new CreateVitaleventUseCase.CreateVitaleventCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateVitaleventUseCase.ActivateVitaleventCommand(
                null, null, null, null, id.toString(), null));

        Vitalevent loaded = get.execute(id.toString());
        assertEquals(VitaleventStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
