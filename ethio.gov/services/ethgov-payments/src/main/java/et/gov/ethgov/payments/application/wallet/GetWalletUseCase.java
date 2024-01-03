package et.gov.ethgov.payments.application.wallet;

import et.gov.ethgov.payments.domain.wallet.Wallet;
import et.gov.ethgov.payments.domain.wallet.WalletId;
import et.gov.ethgov.payments.domain.wallet.WalletRepository;
import java.util.Objects;

public final class GetWalletUseCase {
    private final WalletRepository repository;

    public GetWalletUseCase(WalletRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Wallet execute(String rawId) {
        WalletId id = WalletId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("wallet not found: " + rawId));
    }
}
