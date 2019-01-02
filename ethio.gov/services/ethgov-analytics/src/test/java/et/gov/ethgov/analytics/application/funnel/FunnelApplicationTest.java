package et.gov.ethgov.analytics.application.funnel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryFunnelRepository;
import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FunnelApplicationTest {
    private InMemoryFunnelRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFunnelRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateFunnelUseCase(repository, clock);
        var activate = new ActivateFunnelUseCase(repository, clock);
        var get = new GetFunnelUseCase(repository);

        var id = create.execute(new CreateFunnelUseCase.CreateFunnelCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateFunnelUseCase.ActivateFunnelCommand(
                null, null, null, null, id.toString(), null));

        Funnel loaded = get.execute(id.toString());
        assertEquals(FunnelStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
