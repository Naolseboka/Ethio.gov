package et.gov.ethgov.payments.application.wallet;

import et.gov.ethgov.payments.domain.wallet.Wallet;
import et.gov.ethgov.payments.domain.wallet.WalletId;
import et.gov.ethgov.payments.domain.wallet.WalletRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateWalletUseCase {
    private final WalletRepository repository;
    private final Clock clock;

    public CreateWalletUseCase(WalletRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WalletId execute(CreateWalletCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Wallet aggregate = Wallet.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateWalletCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String walletId,
            String reason) {}
}
