package et.gov.ethgov.notification.application.template;

import et.gov.ethgov.notification.domain.template.Template;
import et.gov.ethgov.notification.domain.template.TemplateId;
import et.gov.ethgov.notification.domain.template.TemplateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateTemplateUseCase {
    private final TemplateRepository repository;
    private final Clock clock;

    public UpdateTemplateUseCase(TemplateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TemplateId execute(UpdateTemplateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TemplateId id = TemplateId.parse(command.templateId());
        Template aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("template not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateTemplateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String templateId,
            String reason) {}
}
