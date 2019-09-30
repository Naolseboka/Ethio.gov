package et.gov.ethgov.citizen.application.guardianship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryGuardianshipRepository;
import et.gov.ethgov.citizen.domain.guardianship.Guardianship;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuardianshipApplicationTest {
    private InMemoryGuardianshipRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGuardianshipRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateGuardianshipUseCase(repository, clock);
        var activate = new ActivateGuardianshipUseCase(repository, clock);
        var get = new GetGuardianshipUseCase(repository);

        var id = create.execute(new CreateGuardianshipUseCase.CreateGuardianshipCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateGuardianshipUseCase.ActivateGuardianshipCommand(
                null, null, null, null, id.toString(), null));

        Guardianship loaded = get.execute(id.toString());
        assertEquals(GuardianshipStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
