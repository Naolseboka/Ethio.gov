package et.gov.ethgov.reporting.application.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryDatasetRepository;
import et.gov.ethgov.reporting.domain.dataset.Dataset;
import et.gov.ethgov.reporting.domain.dataset.DatasetStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatasetApplicationTest {
    private InMemoryDatasetRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDatasetRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDatasetUseCase(repository, clock);
        var activate = new ActivateDatasetUseCase(repository, clock);
        var get = new GetDatasetUseCase(repository);

        var id = create.execute(new CreateDatasetUseCase.CreateDatasetCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDatasetUseCase.ActivateDatasetCommand(
                null, null, null, null, id.toString(), null));

        Dataset loaded = get.execute(id.toString());
        assertEquals(DatasetStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
