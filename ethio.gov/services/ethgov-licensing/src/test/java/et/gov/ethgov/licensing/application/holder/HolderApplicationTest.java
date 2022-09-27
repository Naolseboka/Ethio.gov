package et.gov.ethgov.licensing.application.holder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryHolderRepository;
import et.gov.ethgov.licensing.domain.holder.Holder;
import et.gov.ethgov.licensing.domain.holder.HolderStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HolderApplicationTest {
    private InMemoryHolderRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHolderRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateHolderUseCase(repository, clock);
        var activate = new ActivateHolderUseCase(repository, clock);
        var get = new GetHolderUseCase(repository);

        var id = create.execute(new CreateHolderUseCase.CreateHolderCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateHolderUseCase.ActivateHolderCommand(
                null, null, null, null, id.toString(), null));

        Holder loaded = get.execute(id.toString());
        assertEquals(HolderStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
