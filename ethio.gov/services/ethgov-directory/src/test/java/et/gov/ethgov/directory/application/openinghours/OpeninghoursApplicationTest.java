package et.gov.ethgov.directory.application.openinghours;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryOpeninghoursRepository;
import et.gov.ethgov.directory.domain.openinghours.Openinghours;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpeninghoursApplicationTest {
    private InMemoryOpeninghoursRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOpeninghoursRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOpeninghoursUseCase(repository, clock);
        var activate = new ActivateOpeninghoursUseCase(repository, clock);
        var get = new GetOpeninghoursUseCase(repository);

        var id = create.execute(new CreateOpeninghoursUseCase.CreateOpeninghoursCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOpeninghoursUseCase.ActivateOpeninghoursCommand(
                null, null, null, null, id.toString(), null));

        Openinghours loaded = get.execute(id.toString());
        assertEquals(OpeninghoursStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
