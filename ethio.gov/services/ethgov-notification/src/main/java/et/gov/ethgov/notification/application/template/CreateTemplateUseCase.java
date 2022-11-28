package et.gov.ethgov.notification.application.template;

import et.gov.ethgov.notification.domain.template.Template;
import et.gov.ethgov.notification.domain.template.TemplateId;
import et.gov.ethgov.notification.domain.template.TemplateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTemplateUseCase {
    private final TemplateRepository repository;
    private final Clock clock;

    public CreateTemplateUseCase(TemplateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TemplateId execute(CreateTemplateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Template aggregate = Template.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTemplateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String templateId,
            String reason) {}
}
