package et.gov.ethgov.document.application.retention;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryRetentionRepository;
import et.gov.ethgov.document.domain.retention.Retention;
import et.gov.ethgov.document.domain.retention.RetentionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RetentionApplicationTest {
    private InMemoryRetentionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRetentionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRetentionUseCase(repository, clock);
        var activate = new ActivateRetentionUseCase(repository, clock);
        var get = new GetRetentionUseCase(repository);

        var id = create.execute(new CreateRetentionUseCase.CreateRetentionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRetentionUseCase.ActivateRetentionCommand(
                null, null, null, null, id.toString(), null));

        Retention loaded = get.execute(id.toString());
        assertEquals(RetentionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
