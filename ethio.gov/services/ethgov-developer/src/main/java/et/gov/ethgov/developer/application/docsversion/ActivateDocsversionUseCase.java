package et.gov.ethgov.developer.application.docsversion;

import et.gov.ethgov.developer.domain.docsversion.Docsversion;
import et.gov.ethgov.developer.domain.docsversion.DocsversionId;
import et.gov.ethgov.developer.domain.docsversion.DocsversionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDocsversionUseCase {
    private final DocsversionRepository repository;
    private final Clock clock;

    public ActivateDocsversionUseCase(DocsversionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DocsversionId execute(ActivateDocsversionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DocsversionId id = DocsversionId.parse(command.docsversionId());
        Docsversion aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("docsversion not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDocsversionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String docsversionId,
            String reason) {}
}
