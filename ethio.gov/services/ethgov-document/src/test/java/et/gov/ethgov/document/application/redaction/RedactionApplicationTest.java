package et.gov.ethgov.document.application.redaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryRedactionRepository;
import et.gov.ethgov.document.domain.redaction.Redaction;
import et.gov.ethgov.document.domain.redaction.RedactionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RedactionApplicationTest {
    private InMemoryRedactionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRedactionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRedactionUseCase(repository, clock);
        var activate = new ActivateRedactionUseCase(repository, clock);
        var get = new GetRedactionUseCase(repository);

        var id = create.execute(new CreateRedactionUseCase.CreateRedactionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRedactionUseCase.ActivateRedactionCommand(
                null, null, null, null, id.toString(), null));

        Redaction loaded = get.execute(id.toString());
        assertEquals(RedactionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
