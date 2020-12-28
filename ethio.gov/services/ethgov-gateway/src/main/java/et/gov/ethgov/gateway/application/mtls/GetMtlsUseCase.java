package et.gov.ethgov.gateway.application.mtls;

import et.gov.ethgov.gateway.domain.mtls.Mtls;
import et.gov.ethgov.gateway.domain.mtls.MtlsId;
import et.gov.ethgov.gateway.domain.mtls.MtlsRepository;
import java.util.Objects;

public final class GetMtlsUseCase {
    private final MtlsRepository repository;

    public GetMtlsUseCase(MtlsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Mtls execute(String rawId) {
        MtlsId id = MtlsId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mtls not found: " + rawId));
    }
}
