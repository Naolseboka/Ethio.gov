package et.gov.ethgov.permits.application.permitapplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryPermitapplicationRepository;
import et.gov.ethgov.permits.domain.permitapplication.Permitapplication;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermitapplicationApplicationTest {
    private InMemoryPermitapplicationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPermitapplicationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePermitapplicationUseCase(repository, clock);
        var activate = new ActivatePermitapplicationUseCase(repository, clock);
        var get = new GetPermitapplicationUseCase(repository);

        var id = create.execute(new CreatePermitapplicationUseCase.CreatePermitapplicationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePermitapplicationUseCase.ActivatePermitapplicationCommand(
                null, null, null, null, id.toString(), null));

        Permitapplication loaded = get.execute(id.toString());
        assertEquals(PermitapplicationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
