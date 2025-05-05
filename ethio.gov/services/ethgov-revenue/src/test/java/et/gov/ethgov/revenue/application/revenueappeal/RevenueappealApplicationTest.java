package et.gov.ethgov.revenue.application.revenueappeal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryRevenueappealRepository;
import et.gov.ethgov.revenue.domain.revenueappeal.Revenueappeal;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RevenueappealApplicationTest {
    private InMemoryRevenueappealRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRevenueappealRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRevenueappealUseCase(repository, clock);
        var activate = new ActivateRevenueappealUseCase(repository, clock);
        var get = new GetRevenueappealUseCase(repository);

        var id = create.execute(new CreateRevenueappealUseCase.CreateRevenueappealCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRevenueappealUseCase.ActivateRevenueappealCommand(
                null, null, null, null, id.toString(), null));

        Revenueappeal loaded = get.execute(id.toString());
        assertEquals(RevenueappealStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
