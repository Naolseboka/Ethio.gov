package et.gov.ethgov.document.application.renderjob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryRenderjobRepository;
import et.gov.ethgov.document.domain.renderjob.Renderjob;
import et.gov.ethgov.document.domain.renderjob.RenderjobStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RenderjobApplicationTest {
    private InMemoryRenderjobRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRenderjobRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRenderjobUseCase(repository, clock);
        var activate = new ActivateRenderjobUseCase(repository, clock);
        var get = new GetRenderjobUseCase(repository);

        var id = create.execute(new CreateRenderjobUseCase.CreateRenderjobCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRenderjobUseCase.ActivateRenderjobCommand(
                null, null, null, null, id.toString(), null));

        Renderjob loaded = get.execute(id.toString());
        assertEquals(RenderjobStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
