package et.gov.ethgov.payments.application.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryPaymentRepository;
import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentApplicationTest {
    private InMemoryPaymentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaymentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePaymentUseCase(repository, clock);
        var activate = new ActivatePaymentUseCase(repository, clock);
        var get = new GetPaymentUseCase(repository);

        var id = create.execute(new CreatePaymentUseCase.CreatePaymentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePaymentUseCase.ActivatePaymentCommand(
                null, null, null, null, id.toString(), null));

        Payment loaded = get.execute(id.toString());
        assertEquals(PaymentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
