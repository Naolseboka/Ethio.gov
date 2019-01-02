package et.gov.ethgov.analytics.application.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryPipelineRepository;
import et.gov.ethgov.analytics.domain.pipeline.Pipeline;
import et.gov.ethgov.analytics.domain.pipeline.PipelineStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PipelineApplicationTest {
    private InMemoryPipelineRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPipelineRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePipelineUseCase(repository, clock);
        var activate = new ActivatePipelineUseCase(repository, clock);
        var get = new GetPipelineUseCase(repository);

        var id = create.execute(new CreatePipelineUseCase.CreatePipelineCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePipelineUseCase.ActivatePipelineCommand(
                null, null, null, null, id.toString(), null));

        Pipeline loaded = get.execute(id.toString());
        assertEquals(PipelineStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
