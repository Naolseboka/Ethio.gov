package et.gov.ethgov.analytics.application.cube;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryCubeRepository;
import et.gov.ethgov.analytics.domain.cube.Cube;
import et.gov.ethgov.analytics.domain.cube.CubeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CubeApplicationTest {
    private InMemoryCubeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCubeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCubeUseCase(repository, clock);
        var activate = new ActivateCubeUseCase(repository, clock);
        var get = new GetCubeUseCase(repository);

        var id = create.execute(new CreateCubeUseCase.CreateCubeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCubeUseCase.ActivateCubeCommand(
                null, null, null, null, id.toString(), null));

        Cube loaded = get.execute(id.toString());
        assertEquals(CubeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
