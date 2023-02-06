package et.gov.ethgov.notification.application.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryDeliveryRepository;
import et.gov.ethgov.notification.domain.delivery.Delivery;
import et.gov.ethgov.notification.domain.delivery.DeliveryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeliveryApplicationTest {
    private InMemoryDeliveryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeliveryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDeliveryUseCase(repository, clock);
        var activate = new ActivateDeliveryUseCase(repository, clock);
        var get = new GetDeliveryUseCase(repository);

        var id = create.execute(new CreateDeliveryUseCase.CreateDeliveryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDeliveryUseCase.ActivateDeliveryCommand(
                null, null, null, null, id.toString(), null));

        Delivery loaded = get.execute(id.toString());
        assertEquals(DeliveryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
