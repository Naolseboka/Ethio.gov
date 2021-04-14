package et.gov.ethgov.gateway.application.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryRouteRepository;
import et.gov.ethgov.gateway.domain.route.Route;
import et.gov.ethgov.gateway.domain.route.RouteStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteApplicationTest {
    private InMemoryRouteRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRouteRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRouteUseCase(repository, clock);
        var activate = new ActivateRouteUseCase(repository, clock);
        var get = new GetRouteUseCase(repository);

        var id = create.execute(new CreateRouteUseCase.CreateRouteCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRouteUseCase.ActivateRouteCommand(
                null, null, null, null, id.toString(), null));

        Route loaded = get.execute(id.toString());
        assertEquals(RouteStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
