package et.gov.ethgov.directory.application.office;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryOfficeRepository;
import et.gov.ethgov.directory.domain.office.Office;
import et.gov.ethgov.directory.domain.office.OfficeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfficeApplicationTest {
    private InMemoryOfficeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOfficeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOfficeUseCase(repository, clock);
        var activate = new ActivateOfficeUseCase(repository, clock);
        var get = new GetOfficeUseCase(repository);

        var id = create.execute(new CreateOfficeUseCase.CreateOfficeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOfficeUseCase.ActivateOfficeCommand(
                null, null, null, null, id.toString(), null));

        Office loaded = get.execute(id.toString());
        assertEquals(OfficeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
