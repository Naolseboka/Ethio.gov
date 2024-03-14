package et.gov.ethgov.payments.application.refund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryRefundRepository;
import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefundApplicationTest {
    private InMemoryRefundRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRefundRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRefundUseCase(repository, clock);
        var activate = new ActivateRefundUseCase(repository, clock);
        var get = new GetRefundUseCase(repository);

        var id = create.execute(new CreateRefundUseCase.CreateRefundCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRefundUseCase.ActivateRefundCommand(
                null, null, null, null, id.toString(), null));

        Refund loaded = get.execute(id.toString());
        assertEquals(RefundStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
