package et.gov.ethgov.opendata.application.portalpage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryPortalpageRepository;
import et.gov.ethgov.opendata.domain.portalpage.Portalpage;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortalpageApplicationTest {
    private InMemoryPortalpageRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPortalpageRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePortalpageUseCase(repository, clock);
        var activate = new ActivatePortalpageUseCase(repository, clock);
        var get = new GetPortalpageUseCase(repository);

        var id = create.execute(new CreatePortalpageUseCase.CreatePortalpageCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePortalpageUseCase.ActivatePortalpageCommand(
                null, null, null, null, id.toString(), null));

        Portalpage loaded = get.execute(id.toString());
        assertEquals(PortalpageStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
