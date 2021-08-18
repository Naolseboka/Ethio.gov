package et.gov.ethgov.gis.application.feature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryFeatureRepository;
import et.gov.ethgov.gis.domain.feature.Feature;
import et.gov.ethgov.gis.domain.feature.FeatureStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeatureApplicationTest {
    private InMemoryFeatureRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFeatureRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateFeatureUseCase(repository, clock);
        var activate = new ActivateFeatureUseCase(repository, clock);
        var get = new GetFeatureUseCase(repository);

        var id = create.execute(new CreateFeatureUseCase.CreateFeatureCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateFeatureUseCase.ActivateFeatureCommand(
                null, null, null, null, id.toString(), null));

        Feature loaded = get.execute(id.toString());
        assertEquals(FeatureStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
