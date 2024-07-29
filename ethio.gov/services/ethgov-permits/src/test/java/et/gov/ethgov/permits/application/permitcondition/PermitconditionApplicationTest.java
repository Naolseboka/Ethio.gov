package et.gov.ethgov.permits.application.permitcondition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryPermitconditionRepository;
import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermitconditionApplicationTest {
    private InMemoryPermitconditionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPermitconditionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePermitconditionUseCase(repository, clock);
        var activate = new ActivatePermitconditionUseCase(repository, clock);
        var get = new GetPermitconditionUseCase(repository);

        var id = create.execute(new CreatePermitconditionUseCase.CreatePermitconditionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePermitconditionUseCase.ActivatePermitconditionCommand(
                null, null, null, null, id.toString(), null));

        Permitcondition loaded = get.execute(id.toString());
        assertEquals(PermitconditionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
