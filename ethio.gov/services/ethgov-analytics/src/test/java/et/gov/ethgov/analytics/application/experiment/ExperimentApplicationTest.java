package et.gov.ethgov.analytics.application.experiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryExperimentRepository;
import et.gov.ethgov.analytics.domain.experiment.Experiment;
import et.gov.ethgov.analytics.domain.experiment.ExperimentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExperimentApplicationTest {
    private InMemoryExperimentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryExperimentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateExperimentUseCase(repository, clock);
        var activate = new ActivateExperimentUseCase(repository, clock);
        var get = new GetExperimentUseCase(repository);

        var id = create.execute(new CreateExperimentUseCase.CreateExperimentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateExperimentUseCase.ActivateExperimentCommand(
                null, null, null, null, id.toString(), null));

        Experiment loaded = get.execute(id.toString());
        assertEquals(ExperimentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
