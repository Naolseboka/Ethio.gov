package et.gov.ethgov.licensing.application.inspection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryInspectionRepository;
import et.gov.ethgov.licensing.domain.inspection.Inspection;
import et.gov.ethgov.licensing.domain.inspection.InspectionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InspectionApplicationTest {
    private InMemoryInspectionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryInspectionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateInspectionUseCase(repository, clock);
        var activate = new ActivateInspectionUseCase(repository, clock);
        var get = new GetInspectionUseCase(repository);

        var id = create.execute(new CreateInspectionUseCase.CreateInspectionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateInspectionUseCase.ActivateInspectionCommand(
                null, null, null, null, id.toString(), null));

        Inspection loaded = get.execute(id.toString());
        assertEquals(InspectionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
