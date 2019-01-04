package et.gov.ethgov.analytics.application.warehouse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryWarehouseRepository;
import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseApplicationTest {
    private InMemoryWarehouseRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWarehouseRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateWarehouseUseCase(repository, clock);
        var activate = new ActivateWarehouseUseCase(repository, clock);
        var get = new GetWarehouseUseCase(repository);

        var id = create.execute(new CreateWarehouseUseCase.CreateWarehouseCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateWarehouseUseCase.ActivateWarehouseCommand(
                null, null, null, null, id.toString(), null));

        Warehouse loaded = get.execute(id.toString());
        assertEquals(WarehouseStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
