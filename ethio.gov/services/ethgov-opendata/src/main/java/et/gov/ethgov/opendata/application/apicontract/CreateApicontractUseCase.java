package et.gov.ethgov.opendata.application.apicontract;

import et.gov.ethgov.opendata.domain.apicontract.Apicontract;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractId;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateApicontractUseCase {
    private final ApicontractRepository repository;
    private final Clock clock;

    public CreateApicontractUseCase(ApicontractRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApicontractId execute(CreateApicontractCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Apicontract aggregate = Apicontract.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateApicontractCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String apicontractId,
            String reason) {}
}
