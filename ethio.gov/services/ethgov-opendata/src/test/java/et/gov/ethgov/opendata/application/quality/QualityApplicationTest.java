package et.gov.ethgov.opendata.application.quality;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryQualityRepository;
import et.gov.ethgov.opendata.domain.quality.Quality;
import et.gov.ethgov.opendata.domain.quality.QualityStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QualityApplicationTest {
    private InMemoryQualityRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryQualityRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateQualityUseCase(repository, clock);
        var activate = new ActivateQualityUseCase(repository, clock);
        var get = new GetQualityUseCase(repository);

        var id = create.execute(new CreateQualityUseCase.CreateQualityCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateQualityUseCase.ActivateQualityCommand(
                null, null, null, null, id.toString(), null));

        Quality loaded = get.execute(id.toString());
        assertEquals(QualityStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
