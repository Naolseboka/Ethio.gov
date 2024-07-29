package et.gov.ethgov.permits.application.permitinspection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryPermitinspectionRepository;
import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermitinspectionApplicationTest {
    private InMemoryPermitinspectionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPermitinspectionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePermitinspectionUseCase(repository, clock);
        var activate = new ActivatePermitinspectionUseCase(repository, clock);
        var get = new GetPermitinspectionUseCase(repository);

        var id = create.execute(new CreatePermitinspectionUseCase.CreatePermitinspectionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePermitinspectionUseCase.ActivatePermitinspectionCommand(
                null, null, null, null, id.toString(), null));

        Permitinspection loaded = get.execute(id.toString());
        assertEquals(PermitinspectionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
