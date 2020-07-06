package et.gov.ethgov.directory.application.publiclisting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryPubliclistingRepository;
import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PubliclistingApplicationTest {
    private InMemoryPubliclistingRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPubliclistingRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePubliclistingUseCase(repository, clock);
        var activate = new ActivatePubliclistingUseCase(repository, clock);
        var get = new GetPubliclistingUseCase(repository);

        var id = create.execute(new CreatePubliclistingUseCase.CreatePubliclistingCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePubliclistingUseCase.ActivatePubliclistingCommand(
                null, null, null, null, id.toString(), null));

        Publiclisting loaded = get.execute(id.toString());
        assertEquals(PubliclistingStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
