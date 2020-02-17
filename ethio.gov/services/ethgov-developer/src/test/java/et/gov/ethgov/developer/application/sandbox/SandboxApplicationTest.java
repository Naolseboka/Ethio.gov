package et.gov.ethgov.developer.application.sandbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemorySandboxRepository;
import et.gov.ethgov.developer.domain.sandbox.Sandbox;
import et.gov.ethgov.developer.domain.sandbox.SandboxStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SandboxApplicationTest {
    private InMemorySandboxRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySandboxRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSandboxUseCase(repository, clock);
        var activate = new ActivateSandboxUseCase(repository, clock);
        var get = new GetSandboxUseCase(repository);

        var id = create.execute(new CreateSandboxUseCase.CreateSandboxCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSandboxUseCase.ActivateSandboxCommand(
                null, null, null, null, id.toString(), null));

        Sandbox loaded = get.execute(id.toString());
        assertEquals(SandboxStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
