package et.gov.ethgov.permits.application.permit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryPermitRepository;
import et.gov.ethgov.permits.domain.permit.Permit;
import et.gov.ethgov.permits.domain.permit.PermitStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermitApplicationTest {
    private InMemoryPermitRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPermitRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePermitUseCase(repository, clock);
        var activate = new ActivatePermitUseCase(repository, clock);
        var get = new GetPermitUseCase(repository);

        var id = create.execute(new CreatePermitUseCase.CreatePermitCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePermitUseCase.ActivatePermitCommand(
                null, null, null, null, id.toString(), null));

        Permit loaded = get.execute(id.toString());
        assertEquals(PermitStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
