package et.gov.ethgov.document.application.template;

import et.gov.ethgov.document.domain.template.Template;
import et.gov.ethgov.document.domain.template.TemplateId;
import et.gov.ethgov.document.domain.template.TemplateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTemplateUseCase {
    private final TemplateRepository repository;
    private final Clock clock;

    public ActivateTemplateUseCase(TemplateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TemplateId execute(ActivateTemplateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TemplateId id = TemplateId.parse(command.templateId());
        Template aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("template not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTemplateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String templateId,
            String reason) {}
}
