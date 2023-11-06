package et.gov.ethgov.organization.application.successor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemorySuccessorRepository;
import et.gov.ethgov.organization.domain.successor.Successor;
import et.gov.ethgov.organization.domain.successor.SuccessorStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuccessorApplicationTest {
    private InMemorySuccessorRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySuccessorRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSuccessorUseCase(repository, clock);
        var activate = new ActivateSuccessorUseCase(repository, clock);
        var get = new GetSuccessorUseCase(repository);

        var id = create.execute(new CreateSuccessorUseCase.CreateSuccessorCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSuccessorUseCase.ActivateSuccessorCommand(
                null, null, null, null, id.toString(), null));

        Successor loaded = get.execute(id.toString());
        assertEquals(SuccessorStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
