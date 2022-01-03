package et.gov.ethgov.identity.application.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemorySessionRepository;
import et.gov.ethgov.identity.domain.session.Session;
import et.gov.ethgov.identity.domain.session.SessionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionApplicationTest {
    private InMemorySessionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySessionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSessionUseCase(repository, clock);
        var activate = new ActivateSessionUseCase(repository, clock);
        var get = new GetSessionUseCase(repository);

        var id = create.execute(new CreateSessionUseCase.CreateSessionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSessionUseCase.ActivateSessionCommand(
                null, null, null, null, id.toString(), null));

        Session loaded = get.execute(id.toString());
        assertEquals(SessionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
