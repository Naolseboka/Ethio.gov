package et.gov.ethgov.reporting.application.workbook;

import et.gov.ethgov.reporting.domain.workbook.Workbook;
import et.gov.ethgov.reporting.domain.workbook.WorkbookId;
import et.gov.ethgov.reporting.domain.workbook.WorkbookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateWorkbookUseCase {
    private final WorkbookRepository repository;
    private final Clock clock;

    public UpdateWorkbookUseCase(WorkbookRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WorkbookId execute(UpdateWorkbookCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WorkbookId id = WorkbookId.parse(command.workbookId());
        Workbook aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("workbook not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateWorkbookCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String workbookId,
            String reason) {}
}
