package et.gov.ethgov.notification.application.template;

import et.gov.ethgov.notification.domain.template.Template;
import et.gov.ethgov.notification.domain.template.TemplateId;
import et.gov.ethgov.notification.domain.template.TemplateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendTemplateUseCase {
    private final TemplateRepository repository;
    private final Clock clock;

    public SuspendTemplateUseCase(TemplateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TemplateId execute(SuspendTemplateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TemplateId id = TemplateId.parse(command.templateId());
        Template aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("template not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendTemplateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String templateId,
            String reason) {}
}
