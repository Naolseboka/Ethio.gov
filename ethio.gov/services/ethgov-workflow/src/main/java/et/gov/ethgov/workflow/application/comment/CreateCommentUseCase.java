package et.gov.ethgov.workflow.application.comment;

import et.gov.ethgov.workflow.domain.comment.Comment;
import et.gov.ethgov.workflow.domain.comment.CommentId;
import et.gov.ethgov.workflow.domain.comment.CommentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCommentUseCase {
    private final CommentRepository repository;
    private final Clock clock;

    public CreateCommentUseCase(CommentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CommentId execute(CreateCommentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Comment aggregate = Comment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCommentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String commentId,
            String reason) {}
}
