package et.gov.ethgov.licensing.application.suspension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemorySuspensionRepository;
import et.gov.ethgov.licensing.domain.suspension.Suspension;
import et.gov.ethgov.licensing.domain.suspension.SuspensionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuspensionApplicationTest {
    private InMemorySuspensionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySuspensionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSuspensionUseCase(repository, clock);
        var activate = new ActivateSuspensionUseCase(repository, clock);
        var get = new GetSuspensionUseCase(repository);

        var id = create.execute(new CreateSuspensionUseCase.CreateSuspensionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSuspensionUseCase.ActivateSuspensionCommand(
                null, null, null, null, id.toString(), null));

        Suspension loaded = get.execute(id.toString());
        assertEquals(SuspensionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
