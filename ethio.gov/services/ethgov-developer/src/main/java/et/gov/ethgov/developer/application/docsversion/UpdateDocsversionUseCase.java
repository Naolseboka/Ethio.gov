package et.gov.ethgov.developer.application.docsversion;

import et.gov.ethgov.developer.domain.docsversion.Docsversion;
import et.gov.ethgov.developer.domain.docsversion.DocsversionId;
import et.gov.ethgov.developer.domain.docsversion.DocsversionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDocsversionUseCase {
    private final DocsversionRepository repository;
    private final Clock clock;

    public UpdateDocsversionUseCase(DocsversionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DocsversionId execute(UpdateDocsversionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DocsversionId id = DocsversionId.parse(command.docsversionId());
        Docsversion aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("docsversion not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDocsversionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String docsversionId,
            String reason) {}
}
