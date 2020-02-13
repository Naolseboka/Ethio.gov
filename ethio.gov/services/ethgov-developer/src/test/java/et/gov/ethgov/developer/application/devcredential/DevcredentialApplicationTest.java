package et.gov.ethgov.developer.application.devcredential;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryDevcredentialRepository;
import et.gov.ethgov.developer.domain.devcredential.Devcredential;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DevcredentialApplicationTest {
    private InMemoryDevcredentialRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDevcredentialRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDevcredentialUseCase(repository, clock);
        var activate = new ActivateDevcredentialUseCase(repository, clock);
        var get = new GetDevcredentialUseCase(repository);

        var id = create.execute(new CreateDevcredentialUseCase.CreateDevcredentialCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDevcredentialUseCase.ActivateDevcredentialCommand(
                null, null, null, null, id.toString(), null));

        Devcredential loaded = get.execute(id.toString());
        assertEquals(DevcredentialStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
