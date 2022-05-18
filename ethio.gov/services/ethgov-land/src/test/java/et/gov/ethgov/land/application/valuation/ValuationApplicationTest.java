package et.gov.ethgov.land.application.valuation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryValuationRepository;
import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValuationApplicationTest {
    private InMemoryValuationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryValuationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateValuationUseCase(repository, clock);
        var activate = new ActivateValuationUseCase(repository, clock);
        var get = new GetValuationUseCase(repository);

        var id = create.execute(new CreateValuationUseCase.CreateValuationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateValuationUseCase.ActivateValuationCommand(
                null, null, null, null, id.toString(), null));

        Valuation loaded = get.execute(id.toString());
        assertEquals(ValuationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
