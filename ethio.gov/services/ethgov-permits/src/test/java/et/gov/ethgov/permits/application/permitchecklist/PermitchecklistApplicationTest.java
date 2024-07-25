package et.gov.ethgov.permits.application.permitchecklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryPermitchecklistRepository;
import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermitchecklistApplicationTest {
    private InMemoryPermitchecklistRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPermitchecklistRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePermitchecklistUseCase(repository, clock);
        var activate = new ActivatePermitchecklistUseCase(repository, clock);
        var get = new GetPermitchecklistUseCase(repository);

        var id = create.execute(new CreatePermitchecklistUseCase.CreatePermitchecklistCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePermitchecklistUseCase.ActivatePermitchecklistCommand(
                null, null, null, null, id.toString(), null));

        Permitchecklist loaded = get.execute(id.toString());
        assertEquals(PermitchecklistStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
