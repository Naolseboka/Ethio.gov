package et.gov.ethgov.payments.application.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryInvoiceRepository;
import et.gov.ethgov.payments.domain.invoice.Invoice;
import et.gov.ethgov.payments.domain.invoice.InvoiceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceApplicationTest {
    private InMemoryInvoiceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryInvoiceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateInvoiceUseCase(repository, clock);
        var activate = new ActivateInvoiceUseCase(repository, clock);
        var get = new GetInvoiceUseCase(repository);

        var id = create.execute(new CreateInvoiceUseCase.CreateInvoiceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateInvoiceUseCase.ActivateInvoiceCommand(
                null, null, null, null, id.toString(), null));

        Invoice loaded = get.execute(id.toString());
        assertEquals(InvoiceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
