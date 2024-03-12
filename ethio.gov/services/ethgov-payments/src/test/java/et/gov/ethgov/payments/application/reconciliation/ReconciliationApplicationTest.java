package et.gov.ethgov.payments.application.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryReconciliationRepository;
import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReconciliationApplicationTest {
    private InMemoryReconciliationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReconciliationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateReconciliationUseCase(repository, clock);
        var activate = new ActivateReconciliationUseCase(repository, clock);
        var get = new GetReconciliationUseCase(repository);

        var id = create.execute(new CreateReconciliationUseCase.CreateReconciliationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateReconciliationUseCase.ActivateReconciliationCommand(
                null, null, null, null, id.toString(), null));

        Reconciliation loaded = get.execute(id.toString());
        assertEquals(ReconciliationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
