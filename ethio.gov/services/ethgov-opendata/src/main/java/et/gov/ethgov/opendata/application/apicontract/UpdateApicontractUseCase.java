package et.gov.ethgov.opendata.application.apicontract;

import et.gov.ethgov.opendata.domain.apicontract.Apicontract;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractId;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateApicontractUseCase {
    private final ApicontractRepository repository;
    private final Clock clock;

    public UpdateApicontractUseCase(ApicontractRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApicontractId execute(UpdateApicontractCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ApicontractId id = ApicontractId.parse(command.apicontractId());
        Apicontract aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("apicontract not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateApicontractCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String apicontractId,
            String reason) {}
}
