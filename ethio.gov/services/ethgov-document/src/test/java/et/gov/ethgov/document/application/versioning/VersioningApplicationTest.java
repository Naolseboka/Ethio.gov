package et.gov.ethgov.document.application.versioning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryVersioningRepository;
import et.gov.ethgov.document.domain.versioning.Versioning;
import et.gov.ethgov.document.domain.versioning.VersioningStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersioningApplicationTest {
    private InMemoryVersioningRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVersioningRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateVersioningUseCase(repository, clock);
        var activate = new ActivateVersioningUseCase(repository, clock);
        var get = new GetVersioningUseCase(repository);

        var id = create.execute(new CreateVersioningUseCase.CreateVersioningCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateVersioningUseCase.ActivateVersioningCommand(
                null, null, null, null, id.toString(), null));

        Versioning loaded = get.execute(id.toString());
        assertEquals(VersioningStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
