package et.gov.ethgov.identity.application.delegation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryDelegationRepository;
import et.gov.ethgov.identity.domain.delegation.Delegation;
import et.gov.ethgov.identity.domain.delegation.DelegationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DelegationApplicationTest {
    private InMemoryDelegationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDelegationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDelegationUseCase(repository, clock);
        var activate = new ActivateDelegationUseCase(repository, clock);
        var get = new GetDelegationUseCase(repository);

        var id = create.execute(new CreateDelegationUseCase.CreateDelegationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDelegationUseCase.ActivateDelegationCommand(
                null, null, null, null, id.toString(), null));

        Delegation loaded = get.execute(id.toString());
        assertEquals(DelegationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
