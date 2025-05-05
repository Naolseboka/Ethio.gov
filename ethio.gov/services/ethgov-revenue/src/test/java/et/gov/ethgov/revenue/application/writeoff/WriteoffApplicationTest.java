package et.gov.ethgov.revenue.application.writeoff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryWriteoffRepository;
import et.gov.ethgov.revenue.domain.writeoff.Writeoff;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WriteoffApplicationTest {
    private InMemoryWriteoffRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWriteoffRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateWriteoffUseCase(repository, clock);
        var activate = new ActivateWriteoffUseCase(repository, clock);
        var get = new GetWriteoffUseCase(repository);

        var id = create.execute(new CreateWriteoffUseCase.CreateWriteoffCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateWriteoffUseCase.ActivateWriteoffCommand(
                null, null, null, null, id.toString(), null));

        Writeoff loaded = get.execute(id.toString());
        assertEquals(WriteoffStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
