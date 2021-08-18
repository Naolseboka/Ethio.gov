package et.gov.ethgov.gis.application.crs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryCrsRepository;
import et.gov.ethgov.gis.domain.crs.Crs;
import et.gov.ethgov.gis.domain.crs.CrsStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CrsApplicationTest {
    private InMemoryCrsRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCrsRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCrsUseCase(repository, clock);
        var activate = new ActivateCrsUseCase(repository, clock);
        var get = new GetCrsUseCase(repository);

        var id = create.execute(new CreateCrsUseCase.CreateCrsCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCrsUseCase.ActivateCrsCommand(
                null, null, null, null, id.toString(), null));

        Crs loaded = get.execute(id.toString());
        assertEquals(CrsStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
