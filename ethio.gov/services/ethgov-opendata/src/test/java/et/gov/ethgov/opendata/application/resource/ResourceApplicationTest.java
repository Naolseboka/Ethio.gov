package et.gov.ethgov.opendata.application.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryResourceRepository;
import et.gov.ethgov.opendata.domain.resource.Resource;
import et.gov.ethgov.opendata.domain.resource.ResourceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourceApplicationTest {
    private InMemoryResourceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryResourceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateResourceUseCase(repository, clock);
        var activate = new ActivateResourceUseCase(repository, clock);
        var get = new GetResourceUseCase(repository);

        var id = create.execute(new CreateResourceUseCase.CreateResourceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateResourceUseCase.ActivateResourceCommand(
                null, null, null, null, id.toString(), null));

        Resource loaded = get.execute(id.toString());
        assertEquals(ResourceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
