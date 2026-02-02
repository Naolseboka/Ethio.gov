package et.gov.ethgov.workflow.application.gatewaynode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryGatewaynodeRepository;
import et.gov.ethgov.workflow.domain.gatewaynode.Gatewaynode;
import et.gov.ethgov.workflow.domain.gatewaynode.GatewaynodeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GatewaynodeApplicationTest {
    private InMemoryGatewaynodeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGatewaynodeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateGatewaynodeUseCase(repository, clock);
        var activate = new ActivateGatewaynodeUseCase(repository, clock);
        var get = new GetGatewaynodeUseCase(repository);

        var id = create.execute(new CreateGatewaynodeUseCase.CreateGatewaynodeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateGatewaynodeUseCase.ActivateGatewaynodeCommand(
                null, null, null, null, id.toString(), null));

        Gatewaynode loaded = get.execute(id.toString());
        assertEquals(GatewaynodeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
