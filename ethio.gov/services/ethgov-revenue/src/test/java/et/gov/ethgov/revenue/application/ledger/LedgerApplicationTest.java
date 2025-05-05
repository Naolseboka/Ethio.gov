package et.gov.ethgov.revenue.application.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryLedgerRepository;
import et.gov.ethgov.revenue.domain.ledger.Ledger;
import et.gov.ethgov.revenue.domain.ledger.LedgerStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerApplicationTest {
    private InMemoryLedgerRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLedgerRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateLedgerUseCase(repository, clock);
        var activate = new ActivateLedgerUseCase(repository, clock);
        var get = new GetLedgerUseCase(repository);

        var id = create.execute(new CreateLedgerUseCase.CreateLedgerCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateLedgerUseCase.ActivateLedgerCommand(
                null, null, null, null, id.toString(), null));

        Ledger loaded = get.execute(id.toString());
        assertEquals(LedgerStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
