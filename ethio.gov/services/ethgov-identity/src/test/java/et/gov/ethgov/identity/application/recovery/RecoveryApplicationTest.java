package et.gov.ethgov.identity.application.recovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryRecoveryRepository;
import et.gov.ethgov.identity.domain.recovery.Recovery;
import et.gov.ethgov.identity.domain.recovery.RecoveryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecoveryApplicationTest {
    private InMemoryRecoveryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRecoveryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRecoveryUseCase(repository, clock);
        var activate = new ActivateRecoveryUseCase(repository, clock);
        var get = new GetRecoveryUseCase(repository);

        var id = create.execute(new CreateRecoveryUseCase.CreateRecoveryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRecoveryUseCase.ActivateRecoveryCommand(
                null, null, null, null, id.toString(), null));

        Recovery loaded = get.execute(id.toString());
        assertEquals(RecoveryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
