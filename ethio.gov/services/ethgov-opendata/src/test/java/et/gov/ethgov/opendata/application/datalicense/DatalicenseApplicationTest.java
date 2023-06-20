package et.gov.ethgov.opendata.application.datalicense;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryDatalicenseRepository;
import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatalicenseApplicationTest {
    private InMemoryDatalicenseRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDatalicenseRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDatalicenseUseCase(repository, clock);
        var activate = new ActivateDatalicenseUseCase(repository, clock);
        var get = new GetDatalicenseUseCase(repository);

        var id = create.execute(new CreateDatalicenseUseCase.CreateDatalicenseCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDatalicenseUseCase.ActivateDatalicenseCommand(
                null, null, null, null, id.toString(), null));

        Datalicense loaded = get.execute(id.toString());
        assertEquals(DatalicenseStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
