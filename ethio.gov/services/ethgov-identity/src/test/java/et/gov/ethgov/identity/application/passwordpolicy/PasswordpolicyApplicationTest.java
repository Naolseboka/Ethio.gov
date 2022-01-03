package et.gov.ethgov.identity.application.passwordpolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryPasswordpolicyRepository;
import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordpolicyApplicationTest {
    private InMemoryPasswordpolicyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPasswordpolicyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePasswordpolicyUseCase(repository, clock);
        var activate = new ActivatePasswordpolicyUseCase(repository, clock);
        var get = new GetPasswordpolicyUseCase(repository);

        var id = create.execute(new CreatePasswordpolicyUseCase.CreatePasswordpolicyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePasswordpolicyUseCase.ActivatePasswordpolicyCommand(
                null, null, null, null, id.toString(), null));

        Passwordpolicy loaded = get.execute(id.toString());
        assertEquals(PasswordpolicyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
