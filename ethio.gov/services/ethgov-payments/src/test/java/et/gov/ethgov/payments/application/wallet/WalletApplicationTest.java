package et.gov.ethgov.payments.application.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.payments.adapter.out.persistence.InMemoryWalletRepository;
import et.gov.ethgov.payments.domain.wallet.Wallet;
import et.gov.ethgov.payments.domain.wallet.WalletStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletApplicationTest {
    private InMemoryWalletRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWalletRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateWalletUseCase(repository, clock);
        var activate = new ActivateWalletUseCase(repository, clock);
        var get = new GetWalletUseCase(repository);

        var id = create.execute(new CreateWalletUseCase.CreateWalletCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateWalletUseCase.ActivateWalletCommand(
                null, null, null, null, id.toString(), null));

        Wallet loaded = get.execute(id.toString());
        assertEquals(WalletStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
