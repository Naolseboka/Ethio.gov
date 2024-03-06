package et.gov.ethgov.payments.domain.wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet aggregate);

    Optional<Wallet> findById(WalletId id);

    Optional<Wallet> findByReferenceCode(String tenantId, String referenceCode);

    List<Wallet> findByTenant(String tenantId, int limit);

    void delete(WalletId id);
}
