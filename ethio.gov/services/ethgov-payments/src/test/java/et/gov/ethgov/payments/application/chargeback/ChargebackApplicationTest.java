package et.gov.ethgov.payments.application.chargeback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryChargebackRepository;
import et.gov.ethgov.payments.domain.chargeback.Chargeback;
import et.gov.ethgov.payments.domain.chargeback.ChargebackStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChargebackApplicationTest {
    private InMemoryChargebackRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChargebackRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateChargebackUseCase(repository, clock);
        var activate = new ActivateChargebackUseCase(repository, clock);
        var get = new GetChargebackUseCase(repository);

        var id = create.execute(new CreateChargebackUseCase.CreateChargebackCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateChargebackUseCase.ActivateChargebackCommand(
                null, null, null, null, id.toString(), null));

        Chargeback loaded = get.execute(id.toString());
        assertEquals(ChargebackStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
