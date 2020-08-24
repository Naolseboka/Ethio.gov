package et.gov.ethgov.document.application.signature;

import et.gov.ethgov.document.domain.signature.Signature;
import et.gov.ethgov.document.domain.signature.SignatureId;
import et.gov.ethgov.document.domain.signature.SignatureRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSignatureUseCase {
    private final SignatureRepository repository;
    private final Clock clock;

    public CreateSignatureUseCase(SignatureRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SignatureId execute(CreateSignatureCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Signature aggregate = Signature.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSignatureCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String signatureId,
            String reason) {}
}
