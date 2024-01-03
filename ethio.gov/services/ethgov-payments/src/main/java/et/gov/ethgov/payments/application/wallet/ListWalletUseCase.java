package et.gov.ethgov.payments.application.wallet;

import et.gov.ethgov.payments.domain.wallet.Wallet;
import et.gov.ethgov.payments.domain.wallet.WalletRepository;
import java.util.List;
import java.util.Objects;

public final class ListWalletUseCase {
    private final WalletRepository repository;

    public ListWalletUseCase(WalletRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Wallet> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
