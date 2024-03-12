package et.gov.ethgov.payments.application.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryReceiptRepository;
import et.gov.ethgov.payments.domain.receipt.Receipt;
import et.gov.ethgov.payments.domain.receipt.ReceiptStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReceiptApplicationTest {
    private InMemoryReceiptRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReceiptRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateReceiptUseCase(repository, clock);
        var activate = new ActivateReceiptUseCase(repository, clock);
        var get = new GetReceiptUseCase(repository);

        var id = create.execute(new CreateReceiptUseCase.CreateReceiptCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateReceiptUseCase.ActivateReceiptCommand(
                null, null, null, null, id.toString(), null));

        Receipt loaded = get.execute(id.toString());
        assertEquals(ReceiptStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
