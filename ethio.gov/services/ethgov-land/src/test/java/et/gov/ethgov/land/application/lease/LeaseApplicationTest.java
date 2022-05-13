package et.gov.ethgov.land.application.lease;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryLeaseRepository;
import et.gov.ethgov.land.domain.lease.Lease;
import et.gov.ethgov.land.domain.lease.LeaseStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeaseApplicationTest {
    private InMemoryLeaseRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeaseRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateLeaseUseCase(repository, clock);
        var activate = new ActivateLeaseUseCase(repository, clock);
        var get = new GetLeaseUseCase(repository);

        var id = create.execute(new CreateLeaseUseCase.CreateLeaseCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateLeaseUseCase.ActivateLeaseCommand(
                null, null, null, null, id.toString(), null));

        Lease loaded = get.execute(id.toString());
        assertEquals(LeaseStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
