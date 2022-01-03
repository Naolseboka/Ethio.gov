package et.gov.ethgov.identity.application.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryDeviceRepository;
import et.gov.ethgov.identity.domain.device.Device;
import et.gov.ethgov.identity.domain.device.DeviceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeviceApplicationTest {
    private InMemoryDeviceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeviceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDeviceUseCase(repository, clock);
        var activate = new ActivateDeviceUseCase(repository, clock);
        var get = new GetDeviceUseCase(repository);

        var id = create.execute(new CreateDeviceUseCase.CreateDeviceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDeviceUseCase.ActivateDeviceCommand(
                null, null, null, null, id.toString(), null));

        Device loaded = get.execute(id.toString());
        assertEquals(DeviceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
