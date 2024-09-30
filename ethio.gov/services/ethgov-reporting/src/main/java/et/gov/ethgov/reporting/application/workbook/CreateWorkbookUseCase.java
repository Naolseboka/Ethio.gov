package et.gov.ethgov.reporting.application.workbook;

import et.gov.ethgov.reporting.domain.workbook.Workbook;
import et.gov.ethgov.reporting.domain.workbook.WorkbookId;
import et.gov.ethgov.reporting.domain.workbook.WorkbookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateWorkbookUseCase {
    private final WorkbookRepository repository;
    private final Clock clock;

    public CreateWorkbookUseCase(WorkbookRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WorkbookId execute(CreateWorkbookCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Workbook aggregate = Workbook.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateWorkbookCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String workbookId,
            String reason) {}
}
