package et.gov.ethgov.land.application.easement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryEasementRepository;
import et.gov.ethgov.land.domain.easement.Easement;
import et.gov.ethgov.land.domain.easement.EasementStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EasementApplicationTest {
    private InMemoryEasementRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEasementRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEasementUseCase(repository, clock);
        var activate = new ActivateEasementUseCase(repository, clock);
        var get = new GetEasementUseCase(repository);

        var id = create.execute(new CreateEasementUseCase.CreateEasementCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEasementUseCase.ActivateEasementCommand(
                null, null, null, null, id.toString(), null));

        Easement loaded = get.execute(id.toString());
        assertEquals(EasementStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
