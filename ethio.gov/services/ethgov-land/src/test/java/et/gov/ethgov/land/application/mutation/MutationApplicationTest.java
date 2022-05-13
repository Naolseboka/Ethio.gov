package et.gov.ethgov.land.application.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryMutationRepository;
import et.gov.ethgov.land.domain.mutation.Mutation;
import et.gov.ethgov.land.domain.mutation.MutationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MutationApplicationTest {
    private InMemoryMutationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMutationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateMutationUseCase(repository, clock);
        var activate = new ActivateMutationUseCase(repository, clock);
        var get = new GetMutationUseCase(repository);

        var id = create.execute(new CreateMutationUseCase.CreateMutationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateMutationUseCase.ActivateMutationCommand(
                null, null, null, null, id.toString(), null));

        Mutation loaded = get.execute(id.toString());
        assertEquals(MutationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
