package et.gov.ethgov.citizen.application.citizenship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryCitizenshipRepository;
import et.gov.ethgov.citizen.domain.citizenship.Citizenship;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CitizenshipApplicationTest {
    private InMemoryCitizenshipRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCitizenshipRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCitizenshipUseCase(repository, clock);
        var activate = new ActivateCitizenshipUseCase(repository, clock);
        var get = new GetCitizenshipUseCase(repository);

        var id = create.execute(new CreateCitizenshipUseCase.CreateCitizenshipCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCitizenshipUseCase.ActivateCitizenshipCommand(
                null, null, null, null, id.toString(), null));

        Citizenship loaded = get.execute(id.toString());
        assertEquals(CitizenshipStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
