package et.gov.ethgov.revenue.application.rebate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryRebateRepository;
import et.gov.ethgov.revenue.domain.rebate.Rebate;
import et.gov.ethgov.revenue.domain.rebate.RebateStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RebateApplicationTest {
    private InMemoryRebateRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRebateRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRebateUseCase(repository, clock);
        var activate = new ActivateRebateUseCase(repository, clock);
        var get = new GetRebateUseCase(repository);

        var id = create.execute(new CreateRebateUseCase.CreateRebateCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRebateUseCase.ActivateRebateCommand(
                null, null, null, null, id.toString(), null));

        Rebate loaded = get.execute(id.toString());
        assertEquals(RebateStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
