package et.gov.ethgov.identity.application.mfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryMfaRepository;
import et.gov.ethgov.identity.domain.mfa.Mfa;
import et.gov.ethgov.identity.domain.mfa.MfaStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MfaApplicationTest {
    private InMemoryMfaRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMfaRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateMfaUseCase(repository, clock);
        var activate = new ActivateMfaUseCase(repository, clock);
        var get = new GetMfaUseCase(repository);

        var id = create.execute(new CreateMfaUseCase.CreateMfaCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateMfaUseCase.ActivateMfaCommand(
                null, null, null, null, id.toString(), null));

        Mfa loaded = get.execute(id.toString());
        assertEquals(MfaStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
