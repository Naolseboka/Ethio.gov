package et.gov.ethgov.citizen.application.emergencycontact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryEmergencycontactRepository;
import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmergencycontactApplicationTest {
    private InMemoryEmergencycontactRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEmergencycontactRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEmergencycontactUseCase(repository, clock);
        var activate = new ActivateEmergencycontactUseCase(repository, clock);
        var get = new GetEmergencycontactUseCase(repository);

        var id = create.execute(new CreateEmergencycontactUseCase.CreateEmergencycontactCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEmergencycontactUseCase.ActivateEmergencycontactCommand(
                null, null, null, null, id.toString(), null));

        Emergencycontact loaded = get.execute(id.toString());
        assertEquals(EmergencycontactStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
