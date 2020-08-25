package et.gov.ethgov.document.application.signature;

import et.gov.ethgov.document.domain.signature.Signature;
import et.gov.ethgov.document.domain.signature.SignatureId;
import et.gov.ethgov.document.domain.signature.SignatureRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSignatureUseCase {
    private final SignatureRepository repository;
    private final Clock clock;

    public UpdateSignatureUseCase(SignatureRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SignatureId execute(UpdateSignatureCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SignatureId id = SignatureId.parse(command.signatureId());
        Signature aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("signature not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSignatureCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String signatureId,
            String reason) {}
}
