package et.gov.ethgov.search.application.crawljob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryCrawljobRepository;
import et.gov.ethgov.search.domain.crawljob.Crawljob;
import et.gov.ethgov.search.domain.crawljob.CrawljobStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CrawljobApplicationTest {
    private InMemoryCrawljobRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCrawljobRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCrawljobUseCase(repository, clock);
        var activate = new ActivateCrawljobUseCase(repository, clock);
        var get = new GetCrawljobUseCase(repository);

        var id = create.execute(new CreateCrawljobUseCase.CreateCrawljobCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCrawljobUseCase.ActivateCrawljobCommand(
                null, null, null, null, id.toString(), null));

        Crawljob loaded = get.execute(id.toString());
        assertEquals(CrawljobStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
