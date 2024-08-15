package et.gov.ethgov.reporting.application.dashboard;

import et.gov.ethgov.reporting.domain.dashboard.Dashboard;
import et.gov.ethgov.reporting.domain.dashboard.DashboardId;
import et.gov.ethgov.reporting.domain.dashboard.DashboardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDashboardUseCase {
    private final DashboardRepository repository;
    private final Clock clock;

    public CreateDashboardUseCase(DashboardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DashboardId execute(CreateDashboardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Dashboard aggregate = Dashboard.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDashboardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String dashboardId,
            String reason) {}
}
