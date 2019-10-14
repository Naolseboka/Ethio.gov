package et.gov.ethgov.developer.application.apiproduct;

import et.gov.ethgov.developer.domain.apiproduct.Apiproduct;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductId;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateApiproductUseCase {
    private final ApiproductRepository repository;
    private final Clock clock;

    public CreateApiproductUseCase(ApiproductRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApiproductId execute(CreateApiproductCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Apiproduct aggregate = Apiproduct.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateApiproductCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String apiproductId,
            String reason) {}
}
