package et.gov.ethgov.land.application.title;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryTitleRepository;
import et.gov.ethgov.land.domain.title.Title;
import et.gov.ethgov.land.domain.title.TitleStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitleApplicationTest {
    private InMemoryTitleRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTitleRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTitleUseCase(repository, clock);
        var activate = new ActivateTitleUseCase(repository, clock);
        var get = new GetTitleUseCase(repository);

        var id = create.execute(new CreateTitleUseCase.CreateTitleCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTitleUseCase.ActivateTitleCommand(
                null, null, null, null, id.toString(), null));

        Title loaded = get.execute(id.toString());
        assertEquals(TitleStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
