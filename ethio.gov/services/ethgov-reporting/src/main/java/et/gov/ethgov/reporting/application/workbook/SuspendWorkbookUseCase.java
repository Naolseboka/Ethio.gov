package et.gov.ethgov.reporting.application.workbook;

import et.gov.ethgov.reporting.domain.workbook.Workbook;
import et.gov.ethgov.reporting.domain.workbook.WorkbookId;
import et.gov.ethgov.reporting.domain.workbook.WorkbookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendWorkbookUseCase {
    private final WorkbookRepository repository;
    private final Clock clock;

    public SuspendWorkbookUseCase(WorkbookRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WorkbookId execute(SuspendWorkbookCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WorkbookId id = WorkbookId.parse(command.workbookId());
        Workbook aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("workbook not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendWorkbookCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String workbookId,
            String reason) {}
}
