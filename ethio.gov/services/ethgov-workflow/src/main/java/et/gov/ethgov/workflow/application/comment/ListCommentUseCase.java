package et.gov.ethgov.workflow.application.comment;

import et.gov.ethgov.workflow.domain.comment.Comment;
import et.gov.ethgov.workflow.domain.comment.CommentRepository;
import java.util.List;
import java.util.Objects;

public final class ListCommentUseCase {
    private final CommentRepository repository;

    public ListCommentUseCase(CommentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Comment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
