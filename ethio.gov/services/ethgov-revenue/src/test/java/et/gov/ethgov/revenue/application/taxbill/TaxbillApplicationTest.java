package et.gov.ethgov.revenue.application.taxbill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryTaxbillRepository;
import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaxbillApplicationTest {
    private InMemoryTaxbillRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaxbillRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTaxbillUseCase(repository, clock);
        var activate = new ActivateTaxbillUseCase(repository, clock);
        var get = new GetTaxbillUseCase(repository);

        var id = create.execute(new CreateTaxbillUseCase.CreateTaxbillCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTaxbillUseCase.ActivateTaxbillCommand(
                null, null, null, null, id.toString(), null));

        Taxbill loaded = get.execute(id.toString());
        assertEquals(TaxbillStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
