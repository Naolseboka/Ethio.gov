package et.gov.ethgov.workflow.domain.comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment aggregate);

    Optional<Comment> findById(CommentId id);

    Optional<Comment> findByReferenceCode(String tenantId, String referenceCode);

    List<Comment> findByTenant(String tenantId, int limit);

    void delete(CommentId id);
}
