package et.gov.ethgov.gateway.application.cors;

import et.gov.ethgov.gateway.domain.cors.Cors;
import et.gov.ethgov.gateway.domain.cors.CorsId;
import et.gov.ethgov.gateway.domain.cors.CorsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCorsUseCase {
    private final CorsRepository repository;
    private final Clock clock;

    public UpdateCorsUseCase(CorsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CorsId execute(UpdateCorsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CorsId id = CorsId.parse(command.corsId());
        Cors aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cors not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCorsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String corsId,
            String reason) {}
}
