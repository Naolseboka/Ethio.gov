package et.gov.ethgov.land.application.encumbrance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryEncumbranceRepository;
import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncumbranceApplicationTest {
    private InMemoryEncumbranceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEncumbranceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEncumbranceUseCase(repository, clock);
        var activate = new ActivateEncumbranceUseCase(repository, clock);
        var get = new GetEncumbranceUseCase(repository);

        var id = create.execute(new CreateEncumbranceUseCase.CreateEncumbranceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEncumbranceUseCase.ActivateEncumbranceCommand(
                null, null, null, null, id.toString(), null));

        Encumbrance loaded = get.execute(id.toString());
        assertEquals(EncumbranceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
