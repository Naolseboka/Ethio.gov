package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.comment.Comment;
import et.gov.ethgov.workflow.domain.comment.CommentId;
import et.gov.ethgov.workflow.domain.comment.CommentRepository;
import et.gov.ethgov.workflow.domain.comment.CommentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Comment (replaceable with JPA). */
public final class InMemoryCommentRepository implements CommentRepository {
    private final Map<UUID, StoredComment> store = new ConcurrentHashMap<>();

    @Override
    public Comment save(Comment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredComment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Comment> findById(CommentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredComment::toDomain);
    }

    @Override
    public Optional<Comment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredComment::toDomain)
                .findFirst();
    }

    @Override
    public List<Comment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredComment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredComment::toDomain)
                .toList();
    }

    @Override
    public void delete(CommentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredComment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredComment from(Comment aggregate) {
            return new StoredComment(
                    aggregate.id().value(),
                    aggregate.referenceCode(),
                    aggregate.title(),
                    aggregate.description(),
                    aggregate.tenantId(),
                    aggregate.status().name(),
                    aggregate.createdAt(),
                    aggregate.updatedAt(),
                    aggregate.version());
        }

        Comment toDomain() {
            return Comment.rehydrate(
                    CommentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CommentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
