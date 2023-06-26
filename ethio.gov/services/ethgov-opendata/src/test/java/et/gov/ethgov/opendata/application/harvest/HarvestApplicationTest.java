package et.gov.ethgov.opendata.application.harvest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryHarvestRepository;
import et.gov.ethgov.opendata.domain.harvest.Harvest;
import et.gov.ethgov.opendata.domain.harvest.HarvestStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HarvestApplicationTest {
    private InMemoryHarvestRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHarvestRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateHarvestUseCase(repository, clock);
        var activate = new ActivateHarvestUseCase(repository, clock);
        var get = new GetHarvestUseCase(repository);

        var id = create.execute(new CreateHarvestUseCase.CreateHarvestCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateHarvestUseCase.ActivateHarvestCommand(
                null, null, null, null, id.toString(), null));

        Harvest loaded = get.execute(id.toString());
        assertEquals(HarvestStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
