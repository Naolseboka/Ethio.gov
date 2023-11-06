package et.gov.ethgov.organization.application.rolebinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryRolebindingRepository;
import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolebindingApplicationTest {
    private InMemoryRolebindingRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRolebindingRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRolebindingUseCase(repository, clock);
        var activate = new ActivateRolebindingUseCase(repository, clock);
        var get = new GetRolebindingUseCase(repository);

        var id = create.execute(new CreateRolebindingUseCase.CreateRolebindingCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRolebindingUseCase.ActivateRolebindingCommand(
                null, null, null, null, id.toString(), null));

        Rolebinding loaded = get.execute(id.toString());
        assertEquals(RolebindingStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
