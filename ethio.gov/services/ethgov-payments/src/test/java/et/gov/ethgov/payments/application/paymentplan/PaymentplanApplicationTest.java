package et.gov.ethgov.payments.application.paymentplan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryPaymentplanRepository;
import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentplanApplicationTest {
    private InMemoryPaymentplanRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaymentplanRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePaymentplanUseCase(repository, clock);
        var activate = new ActivatePaymentplanUseCase(repository, clock);
        var get = new GetPaymentplanUseCase(repository);

        var id = create.execute(new CreatePaymentplanUseCase.CreatePaymentplanCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePaymentplanUseCase.ActivatePaymentplanCommand(
                null, null, null, null, id.toString(), null));

        Paymentplan loaded = get.execute(id.toString());
        assertEquals(PaymentplanStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
