package et.gov.ethgov.developer.application.docsversion;

import et.gov.ethgov.developer.domain.docsversion.Docsversion;
import et.gov.ethgov.developer.domain.docsversion.DocsversionId;
import et.gov.ethgov.developer.domain.docsversion.DocsversionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDocsversionUseCase {
    private final DocsversionRepository repository;
    private final Clock clock;

    public CreateDocsversionUseCase(DocsversionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DocsversionId execute(CreateDocsversionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Docsversion aggregate = Docsversion.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDocsversionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String docsversionId,
            String reason) {}
}
