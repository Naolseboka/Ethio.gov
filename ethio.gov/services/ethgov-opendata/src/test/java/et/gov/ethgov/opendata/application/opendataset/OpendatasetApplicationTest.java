package et.gov.ethgov.opendata.application.opendataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryOpendatasetRepository;
import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpendatasetApplicationTest {
    private InMemoryOpendatasetRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOpendatasetRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOpendatasetUseCase(repository, clock);
        var activate = new ActivateOpendatasetUseCase(repository, clock);
        var get = new GetOpendatasetUseCase(repository);

        var id = create.execute(new CreateOpendatasetUseCase.CreateOpendatasetCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOpendatasetUseCase.ActivateOpendatasetCommand(
                null, null, null, null, id.toString(), null));

        Opendataset loaded = get.execute(id.toString());
        assertEquals(OpendatasetStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
