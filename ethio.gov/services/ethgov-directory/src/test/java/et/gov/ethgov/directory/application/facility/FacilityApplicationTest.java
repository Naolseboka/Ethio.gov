package et.gov.ethgov.directory.application.facility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryFacilityRepository;
import et.gov.ethgov.directory.domain.facility.Facility;
import et.gov.ethgov.directory.domain.facility.FacilityStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FacilityApplicationTest {
    private InMemoryFacilityRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFacilityRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateFacilityUseCase(repository, clock);
        var activate = new ActivateFacilityUseCase(repository, clock);
        var get = new GetFacilityUseCase(repository);

        var id = create.execute(new CreateFacilityUseCase.CreateFacilityCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateFacilityUseCase.ActivateFacilityCommand(
                null, null, null, null, id.toString(), null));

        Facility loaded = get.execute(id.toString());
        assertEquals(FacilityStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
