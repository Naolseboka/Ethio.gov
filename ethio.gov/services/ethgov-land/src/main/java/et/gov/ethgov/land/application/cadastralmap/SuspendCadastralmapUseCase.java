package et.gov.ethgov.land.application.cadastralmap;

import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapId;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendCadastralmapUseCase {
    private final CadastralmapRepository repository;
    private final Clock clock;

    public SuspendCadastralmapUseCase(CadastralmapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CadastralmapId execute(SuspendCadastralmapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CadastralmapId id = CadastralmapId.parse(command.cadastralmapId());
        Cadastralmap aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cadastralmap not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendCadastralmapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cadastralmapId,
            String reason) {}
}
