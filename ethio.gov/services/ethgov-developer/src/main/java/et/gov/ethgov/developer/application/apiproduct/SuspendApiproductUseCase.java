package et.gov.ethgov.developer.application.apiproduct;

import et.gov.ethgov.developer.domain.apiproduct.Apiproduct;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductId;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendApiproductUseCase {
    private final ApiproductRepository repository;
    private final Clock clock;

    public SuspendApiproductUseCase(ApiproductRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ApiproductId execute(SuspendApiproductCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ApiproductId id = ApiproductId.parse(command.apiproductId());
        Apiproduct aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("apiproduct not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendApiproductCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String apiproductId,
            String reason) {}
}
