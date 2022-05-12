package et.gov.ethgov.land.application.conflict;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryConflictRepository;
import et.gov.ethgov.land.domain.conflict.Conflict;
import et.gov.ethgov.land.domain.conflict.ConflictStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConflictApplicationTest {
    private InMemoryConflictRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConflictRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateConflictUseCase(repository, clock);
        var activate = new ActivateConflictUseCase(repository, clock);
        var get = new GetConflictUseCase(repository);

        var id = create.execute(new CreateConflictUseCase.CreateConflictCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateConflictUseCase.ActivateConflictCommand(
                null, null, null, null, id.toString(), null));

        Conflict loaded = get.execute(id.toString());
        assertEquals(ConflictStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
