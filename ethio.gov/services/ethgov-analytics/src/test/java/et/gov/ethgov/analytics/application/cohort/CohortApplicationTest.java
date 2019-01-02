package et.gov.ethgov.analytics.application.cohort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryCohortRepository;
import et.gov.ethgov.analytics.domain.cohort.Cohort;
import et.gov.ethgov.analytics.domain.cohort.CohortStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CohortApplicationTest {
    private InMemoryCohortRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCohortRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCohortUseCase(repository, clock);
        var activate = new ActivateCohortUseCase(repository, clock);
        var get = new GetCohortUseCase(repository);

        var id = create.execute(new CreateCohortUseCase.CreateCohortCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCohortUseCase.ActivateCohortCommand(
                null, null, null, null, id.toString(), null));

        Cohort loaded = get.execute(id.toString());
        assertEquals(CohortStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
