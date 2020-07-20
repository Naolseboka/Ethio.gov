package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.signature.Signature;
import et.gov.ethgov.document.domain.signature.SignatureId;
import et.gov.ethgov.document.domain.signature.SignatureRepository;
import et.gov.ethgov.document.domain.signature.SignatureStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Signature (replaceable with JPA). */
public final class InMemorySignatureRepository implements SignatureRepository {
    private final Map<UUID, StoredSignature> store = new ConcurrentHashMap<>();

    @Override
    public Signature save(Signature aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSignature.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Signature> findById(SignatureId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSignature::toDomain);
    }

    @Override
    public Optional<Signature> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSignature::toDomain)
                .findFirst();
    }

    @Override
    public List<Signature> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSignature row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSignature::toDomain)
                .toList();
    }

    @Override
    public void delete(SignatureId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSignature(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSignature from(Signature aggregate) {
            return new StoredSignature(
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

        Signature toDomain() {
            return Signature.rehydrate(
                    SignatureId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SignatureStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
