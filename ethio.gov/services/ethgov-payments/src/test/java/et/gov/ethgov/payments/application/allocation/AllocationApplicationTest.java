package et.gov.ethgov.payments.application.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryAllocationRepository;
import et.gov.ethgov.payments.domain.allocation.Allocation;
import et.gov.ethgov.payments.domain.allocation.AllocationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AllocationApplicationTest {
    private InMemoryAllocationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAllocationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAllocationUseCase(repository, clock);
        var activate = new ActivateAllocationUseCase(repository, clock);
        var get = new GetAllocationUseCase(repository);

        var id = create.execute(new CreateAllocationUseCase.CreateAllocationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAllocationUseCase.ActivateAllocationCommand(
                null, null, null, null, id.toString(), null));

        Allocation loaded = get.execute(id.toString());
        assertEquals(AllocationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
