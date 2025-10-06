package et.gov.ethgov.workflow.application.comment;

import et.gov.ethgov.workflow.domain.comment.Comment;
import et.gov.ethgov.workflow.domain.comment.CommentId;
import et.gov.ethgov.workflow.domain.comment.CommentRepository;
import java.util.Objects;

public final class GetCommentUseCase {
    private final CommentRepository repository;

    public GetCommentUseCase(CommentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Comment execute(String rawId) {
        CommentId id = CommentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("comment not found: " + rawId));
    }
}
