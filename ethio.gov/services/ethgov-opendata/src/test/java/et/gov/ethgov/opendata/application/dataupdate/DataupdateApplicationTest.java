package et.gov.ethgov.opendata.application.dataupdate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryDataupdateRepository;
import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataupdateApplicationTest {
    private InMemoryDataupdateRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDataupdateRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDataupdateUseCase(repository, clock);
        var activate = new ActivateDataupdateUseCase(repository, clock);
        var get = new GetDataupdateUseCase(repository);

        var id = create.execute(new CreateDataupdateUseCase.CreateDataupdateCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDataupdateUseCase.ActivateDataupdateCommand(
                null, null, null, null, id.toString(), null));

        Dataupdate loaded = get.execute(id.toString());
        assertEquals(DataupdateStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
