package et.gov.ethgov.citizen.application.household;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryHouseholdRepository;
import et.gov.ethgov.citizen.domain.household.Household;
import et.gov.ethgov.citizen.domain.household.HouseholdStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HouseholdApplicationTest {
    private InMemoryHouseholdRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHouseholdRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateHouseholdUseCase(repository, clock);
        var activate = new ActivateHouseholdUseCase(repository, clock);
        var get = new GetHouseholdUseCase(repository);

        var id = create.execute(new CreateHouseholdUseCase.CreateHouseholdCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateHouseholdUseCase.ActivateHouseholdCommand(
                null, null, null, null, id.toString(), null));

        Household loaded = get.execute(id.toString());
        assertEquals(HouseholdStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
