package et.gov.ethgov.identity.application.credential;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryCredentialRepository;
import et.gov.ethgov.identity.domain.credential.Credential;
import et.gov.ethgov.identity.domain.credential.CredentialStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CredentialApplicationTest {
    private InMemoryCredentialRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCredentialRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCredentialUseCase(repository, clock);
        var activate = new ActivateCredentialUseCase(repository, clock);
        var get = new GetCredentialUseCase(repository);

        var id = create.execute(new CreateCredentialUseCase.CreateCredentialCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCredentialUseCase.ActivateCredentialCommand(
                null, null, null, null, id.toString(), null));

        Credential loaded = get.execute(id.toString());
        assertEquals(CredentialStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
