package et.gov.ethgov.directory.application.kiosk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryKioskRepository;
import et.gov.ethgov.directory.domain.kiosk.Kiosk;
import et.gov.ethgov.directory.domain.kiosk.KioskStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KioskApplicationTest {
    private InMemoryKioskRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryKioskRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateKioskUseCase(repository, clock);
        var activate = new ActivateKioskUseCase(repository, clock);
        var get = new GetKioskUseCase(repository);

        var id = create.execute(new CreateKioskUseCase.CreateKioskCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateKioskUseCase.ActivateKioskCommand(
                null, null, null, null, id.toString(), null));

        Kiosk loaded = get.execute(id.toString());
        assertEquals(KioskStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
