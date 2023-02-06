package et.gov.ethgov.notification.application.digest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryDigestRepository;
import et.gov.ethgov.notification.domain.digest.Digest;
import et.gov.ethgov.notification.domain.digest.DigestStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DigestApplicationTest {
    private InMemoryDigestRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDigestRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDigestUseCase(repository, clock);
        var activate = new ActivateDigestUseCase(repository, clock);
        var get = new GetDigestUseCase(repository);

        var id = create.execute(new CreateDigestUseCase.CreateDigestCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDigestUseCase.ActivateDigestCommand(
                null, null, null, null, id.toString(), null));

        Digest loaded = get.execute(id.toString());
        assertEquals(DigestStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
