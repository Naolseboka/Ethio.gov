package et.gov.ethgov.document.application.accessgrant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryAccessgrantRepository;
import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessgrantApplicationTest {
    private InMemoryAccessgrantRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccessgrantRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAccessgrantUseCase(repository, clock);
        var activate = new ActivateAccessgrantUseCase(repository, clock);
        var get = new GetAccessgrantUseCase(repository);

        var id = create.execute(new CreateAccessgrantUseCase.CreateAccessgrantCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAccessgrantUseCase.ActivateAccessgrantCommand(
                null, null, null, null, id.toString(), null));

        Accessgrant loaded = get.execute(id.toString());
        assertEquals(AccessgrantStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
