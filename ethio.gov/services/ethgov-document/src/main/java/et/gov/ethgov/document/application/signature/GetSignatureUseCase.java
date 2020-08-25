package et.gov.ethgov.document.application.signature;

import et.gov.ethgov.document.domain.signature.Signature;
import et.gov.ethgov.document.domain.signature.SignatureId;
import et.gov.ethgov.document.domain.signature.SignatureRepository;
import java.util.Objects;

public final class GetSignatureUseCase {
    private final SignatureRepository repository;

    public GetSignatureUseCase(SignatureRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Signature execute(String rawId) {
        SignatureId id = SignatureId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("signature not found: " + rawId));
    }
}
