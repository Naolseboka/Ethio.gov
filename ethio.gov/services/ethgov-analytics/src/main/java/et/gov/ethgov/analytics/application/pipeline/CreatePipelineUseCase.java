package et.gov.ethgov.analytics.application.pipeline;

import et.gov.ethgov.analytics.domain.pipeline.Pipeline;
import et.gov.ethgov.analytics.domain.pipeline.PipelineId;
import et.gov.ethgov.analytics.domain.pipeline.PipelineRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePipelineUseCase {
    private final PipelineRepository repository;
    private final Clock clock;

    public CreatePipelineUseCase(PipelineRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PipelineId execute(CreatePipelineCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Pipeline aggregate = Pipeline.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePipelineCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String pipelineId,
            String reason) {}
}
