package et.gov.ethgov.payments.application.settlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemorySettlementRepository;
import et.gov.ethgov.payments.domain.settlement.Settlement;
import et.gov.ethgov.payments.domain.settlement.SettlementStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettlementApplicationTest {
    private InMemorySettlementRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySettlementRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSettlementUseCase(repository, clock);
        var activate = new ActivateSettlementUseCase(repository, clock);
        var get = new GetSettlementUseCase(repository);

        var id = create.execute(new CreateSettlementUseCase.CreateSettlementCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSettlementUseCase.ActivateSettlementCommand(
                null, null, null, null, id.toString(), null));

        Settlement loaded = get.execute(id.toString());
        assertEquals(SettlementStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
