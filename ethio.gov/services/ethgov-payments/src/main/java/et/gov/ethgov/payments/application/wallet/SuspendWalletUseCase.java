package et.gov.ethgov.payments.application.wallet;

import et.gov.ethgov.payments.domain.wallet.Wallet;
import et.gov.ethgov.payments.domain.wallet.WalletId;
import et.gov.ethgov.payments.domain.wallet.WalletRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendWalletUseCase {
    private final WalletRepository repository;
    private final Clock clock;

    public SuspendWalletUseCase(WalletRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WalletId execute(SuspendWalletCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WalletId id = WalletId.parse(command.walletId());
        Wallet aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("wallet not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendWalletCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String walletId,
            String reason) {}
}
