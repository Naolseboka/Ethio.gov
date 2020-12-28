package et.gov.ethgov.gateway.application.mtls;

import et.gov.ethgov.gateway.domain.mtls.Mtls;
import et.gov.ethgov.gateway.domain.mtls.MtlsId;
import et.gov.ethgov.gateway.domain.mtls.MtlsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateMtlsUseCase {
    private final MtlsRepository repository;
    private final Clock clock;

    public ActivateMtlsUseCase(MtlsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MtlsId execute(ActivateMtlsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MtlsId id = MtlsId.parse(command.mtlsId());
        Mtls aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mtls not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateMtlsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mtlsId,
            String reason) {}
}
