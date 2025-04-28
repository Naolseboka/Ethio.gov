package et.gov.ethgov.revenue.application.arrears;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryArrearsRepository;
import et.gov.ethgov.revenue.domain.arrears.Arrears;
import et.gov.ethgov.revenue.domain.arrears.ArrearsStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrearsApplicationTest {
    private InMemoryArrearsRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArrearsRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateArrearsUseCase(repository, clock);
        var activate = new ActivateArrearsUseCase(repository, clock);
        var get = new GetArrearsUseCase(repository);

        var id = create.execute(new CreateArrearsUseCase.CreateArrearsCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateArrearsUseCase.ActivateArrearsCommand(
                null, null, null, null, id.toString(), null));

        Arrears loaded = get.execute(id.toString());
        assertEquals(ArrearsStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
