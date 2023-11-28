package et.gov.ethgov.payments.application.chargeback;

import et.gov.ethgov.payments.domain.chargeback.Chargeback;
import et.gov.ethgov.payments.domain.chargeback.ChargebackId;
import et.gov.ethgov.payments.domain.chargeback.ChargebackRepository;
import java.util.Objects;

public final class GetChargebackUseCase {
    private final ChargebackRepository repository;

    public GetChargebackUseCase(ChargebackRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Chargeback execute(String rawId) {
        ChargebackId id = ChargebackId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("chargeback not found: " + rawId));
    }
}
