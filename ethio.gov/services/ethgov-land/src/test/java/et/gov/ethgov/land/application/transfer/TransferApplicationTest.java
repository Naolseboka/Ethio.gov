package et.gov.ethgov.land.application.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryTransferRepository;
import et.gov.ethgov.land.domain.transfer.Transfer;
import et.gov.ethgov.land.domain.transfer.TransferStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferApplicationTest {
    private InMemoryTransferRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransferRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTransferUseCase(repository, clock);
        var activate = new ActivateTransferUseCase(repository, clock);
        var get = new GetTransferUseCase(repository);

        var id = create.execute(new CreateTransferUseCase.CreateTransferCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTransferUseCase.ActivateTransferCommand(
                null, null, null, null, id.toString(), null));

        Transfer loaded = get.execute(id.toString());
        assertEquals(TransferStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
