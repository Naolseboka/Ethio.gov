package et.gov.ethgov.land.application.cadastralmap;

import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapId;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCadastralmapUseCase {
    private final CadastralmapRepository repository;
    private final Clock clock;

    public CreateCadastralmapUseCase(CadastralmapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CadastralmapId execute(CreateCadastralmapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Cadastralmap aggregate = Cadastralmap.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCadastralmapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cadastralmapId,
            String reason) {}
}
