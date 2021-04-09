package et.gov.ethgov.gateway.application.authn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryAuthnRepository;
import et.gov.ethgov.gateway.domain.authn.Authn;
import et.gov.ethgov.gateway.domain.authn.AuthnStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthnApplicationTest {
    private InMemoryAuthnRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAuthnRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAuthnUseCase(repository, clock);
        var activate = new ActivateAuthnUseCase(repository, clock);
        var get = new GetAuthnUseCase(repository);

        var id = create.execute(new CreateAuthnUseCase.CreateAuthnCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAuthnUseCase.ActivateAuthnCommand(
                null, null, null, null, id.toString(), null));

        Authn loaded = get.execute(id.toString());
        assertEquals(AuthnStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
