package et.gov.ethgov.organization.application.seal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemorySealRepository;
import et.gov.ethgov.organization.domain.seal.Seal;
import et.gov.ethgov.organization.domain.seal.SealStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SealApplicationTest {
    private InMemorySealRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySealRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSealUseCase(repository, clock);
        var activate = new ActivateSealUseCase(repository, clock);
        var get = new GetSealUseCase(repository);

        var id = create.execute(new CreateSealUseCase.CreateSealCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSealUseCase.ActivateSealCommand(
                null, null, null, null, id.toString(), null));

        Seal loaded = get.execute(id.toString());
        assertEquals(SealStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
