package et.gov.ethgov.permits.application.bond;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryBondRepository;
import et.gov.ethgov.permits.domain.bond.Bond;
import et.gov.ethgov.permits.domain.bond.BondStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BondApplicationTest {
    private InMemoryBondRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBondRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateBondUseCase(repository, clock);
        var activate = new ActivateBondUseCase(repository, clock);
        var get = new GetBondUseCase(repository);

        var id = create.execute(new CreateBondUseCase.CreateBondCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateBondUseCase.ActivateBondCommand(
                null, null, null, null, id.toString(), null));

        Bond loaded = get.execute(id.toString());
        assertEquals(BondStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
