package et.gov.ethgov.identity.application.lockout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryLockoutRepository;
import et.gov.ethgov.identity.domain.lockout.Lockout;
import et.gov.ethgov.identity.domain.lockout.LockoutStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LockoutApplicationTest {
    private InMemoryLockoutRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLockoutRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateLockoutUseCase(repository, clock);
        var activate = new ActivateLockoutUseCase(repository, clock);
        var get = new GetLockoutUseCase(repository);

        var id = create.execute(new CreateLockoutUseCase.CreateLockoutCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateLockoutUseCase.ActivateLockoutCommand(
                null, null, null, null, id.toString(), null));

        Lockout loaded = get.execute(id.toString());
        assertEquals(LockoutStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
