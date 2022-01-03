package et.gov.ethgov.identity.application.oauthclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryOauthclientRepository;
import et.gov.ethgov.identity.domain.oauthclient.Oauthclient;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OauthclientApplicationTest {
    private InMemoryOauthclientRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOauthclientRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOauthclientUseCase(repository, clock);
        var activate = new ActivateOauthclientUseCase(repository, clock);
        var get = new GetOauthclientUseCase(repository);

        var id = create.execute(new CreateOauthclientUseCase.CreateOauthclientCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOauthclientUseCase.ActivateOauthclientCommand(
                null, null, null, null, id.toString(), null));

        Oauthclient loaded = get.execute(id.toString());
        assertEquals(OauthclientStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
