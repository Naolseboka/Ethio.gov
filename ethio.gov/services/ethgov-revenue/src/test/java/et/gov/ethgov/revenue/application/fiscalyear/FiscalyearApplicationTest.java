package et.gov.ethgov.revenue.application.fiscalyear;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryFiscalyearRepository;
import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FiscalyearApplicationTest {
    private InMemoryFiscalyearRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFiscalyearRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateFiscalyearUseCase(repository, clock);
        var activate = new ActivateFiscalyearUseCase(repository, clock);
        var get = new GetFiscalyearUseCase(repository);

        var id = create.execute(new CreateFiscalyearUseCase.CreateFiscalyearCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateFiscalyearUseCase.ActivateFiscalyearCommand(
                null, null, null, null, id.toString(), null));

        Fiscalyear loaded = get.execute(id.toString());
        assertEquals(FiscalyearStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
