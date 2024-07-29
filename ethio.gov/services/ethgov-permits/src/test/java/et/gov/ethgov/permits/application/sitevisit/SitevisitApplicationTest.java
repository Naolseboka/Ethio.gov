package et.gov.ethgov.permits.application.sitevisit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemorySitevisitRepository;
import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SitevisitApplicationTest {
    private InMemorySitevisitRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySitevisitRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSitevisitUseCase(repository, clock);
        var activate = new ActivateSitevisitUseCase(repository, clock);
        var get = new GetSitevisitUseCase(repository);

        var id = create.execute(new CreateSitevisitUseCase.CreateSitevisitCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSitevisitUseCase.ActivateSitevisitCommand(
                null, null, null, null, id.toString(), null));

        Sitevisit loaded = get.execute(id.toString());
        assertEquals(SitevisitStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
