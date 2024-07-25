package et.gov.ethgov.permits.application.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryExtensionRepository;
import et.gov.ethgov.permits.domain.extension.Extension;
import et.gov.ethgov.permits.domain.extension.ExtensionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtensionApplicationTest {
    private InMemoryExtensionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryExtensionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateExtensionUseCase(repository, clock);
        var activate = new ActivateExtensionUseCase(repository, clock);
        var get = new GetExtensionUseCase(repository);

        var id = create.execute(new CreateExtensionUseCase.CreateExtensionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateExtensionUseCase.ActivateExtensionCommand(
                null, null, null, null, id.toString(), null));

        Extension loaded = get.execute(id.toString());
        assertEquals(ExtensionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
