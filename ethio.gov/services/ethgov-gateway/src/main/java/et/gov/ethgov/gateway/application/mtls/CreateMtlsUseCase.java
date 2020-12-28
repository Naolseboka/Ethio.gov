package et.gov.ethgov.gateway.application.mtls;

import et.gov.ethgov.gateway.domain.mtls.Mtls;
import et.gov.ethgov.gateway.domain.mtls.MtlsId;
import et.gov.ethgov.gateway.domain.mtls.MtlsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateMtlsUseCase {
    private final MtlsRepository repository;
    private final Clock clock;

    public CreateMtlsUseCase(MtlsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MtlsId execute(CreateMtlsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Mtls aggregate = Mtls.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateMtlsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mtlsId,
            String reason) {}
}
