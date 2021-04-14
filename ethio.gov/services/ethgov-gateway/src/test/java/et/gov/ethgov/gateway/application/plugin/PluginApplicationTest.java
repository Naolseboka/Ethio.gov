package et.gov.ethgov.gateway.application.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryPluginRepository;
import et.gov.ethgov.gateway.domain.plugin.Plugin;
import et.gov.ethgov.gateway.domain.plugin.PluginStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PluginApplicationTest {
    private InMemoryPluginRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPluginRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePluginUseCase(repository, clock);
        var activate = new ActivatePluginUseCase(repository, clock);
        var get = new GetPluginUseCase(repository);

        var id = create.execute(new CreatePluginUseCase.CreatePluginCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePluginUseCase.ActivatePluginCommand(
                null, null, null, null, id.toString(), null));

        Plugin loaded = get.execute(id.toString());
        assertEquals(PluginStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
