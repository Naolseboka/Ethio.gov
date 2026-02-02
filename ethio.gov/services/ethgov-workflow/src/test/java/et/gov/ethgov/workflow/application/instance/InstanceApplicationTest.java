package et.gov.ethgov.workflow.application.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryInstanceRepository;
import et.gov.ethgov.workflow.domain.instance.Instance;
import et.gov.ethgov.workflow.domain.instance.InstanceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstanceApplicationTest {
    private InMemoryInstanceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryInstanceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateInstanceUseCase(repository, clock);
        var activate = new ActivateInstanceUseCase(repository, clock);
        var get = new GetInstanceUseCase(repository);

        var id = create.execute(new CreateInstanceUseCase.CreateInstanceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateInstanceUseCase.ActivateInstanceCommand(
                null, null, null, null, id.toString(), null));

        Instance loaded = get.execute(id.toString());
        assertEquals(InstanceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
