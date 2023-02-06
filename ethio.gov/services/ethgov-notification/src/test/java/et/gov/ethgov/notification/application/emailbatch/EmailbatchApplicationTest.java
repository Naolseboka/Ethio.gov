package et.gov.ethgov.notification.application.emailbatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryEmailbatchRepository;
import et.gov.ethgov.notification.domain.emailbatch.Emailbatch;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmailbatchApplicationTest {
    private InMemoryEmailbatchRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEmailbatchRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEmailbatchUseCase(repository, clock);
        var activate = new ActivateEmailbatchUseCase(repository, clock);
        var get = new GetEmailbatchUseCase(repository);

        var id = create.execute(new CreateEmailbatchUseCase.CreateEmailbatchCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEmailbatchUseCase.ActivateEmailbatchCommand(
                null, null, null, null, id.toString(), null));

        Emailbatch loaded = get.execute(id.toString());
        assertEquals(EmailbatchStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
