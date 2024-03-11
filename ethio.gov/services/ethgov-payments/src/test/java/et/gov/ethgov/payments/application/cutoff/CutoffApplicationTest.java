package et.gov.ethgov.payments.application.cutoff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryCutoffRepository;
import et.gov.ethgov.payments.domain.cutoff.Cutoff;
import et.gov.ethgov.payments.domain.cutoff.CutoffStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CutoffApplicationTest {
    private InMemoryCutoffRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCutoffRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCutoffUseCase(repository, clock);
        var activate = new ActivateCutoffUseCase(repository, clock);
        var get = new GetCutoffUseCase(repository);

        var id = create.execute(new CreateCutoffUseCase.CreateCutoffCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCutoffUseCase.ActivateCutoffCommand(
                null, null, null, null, id.toString(), null));

        Cutoff loaded = get.execute(id.toString());
        assertEquals(CutoffStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
