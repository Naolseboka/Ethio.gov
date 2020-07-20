package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.template.Template;
import et.gov.ethgov.document.domain.template.TemplateId;
import et.gov.ethgov.document.domain.template.TemplateRepository;
import et.gov.ethgov.document.domain.template.TemplateStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Template (replaceable with JPA). */
public final class InMemoryTemplateRepository implements TemplateRepository {
    private final Map<UUID, StoredTemplate> store = new ConcurrentHashMap<>();

    @Override
    public Template save(Template aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTemplate.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Template> findById(TemplateId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTemplate::toDomain);
    }

    @Override
    public Optional<Template> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTemplate::toDomain)
                .findFirst();
    }

    @Override
    public List<Template> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTemplate row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTemplate::toDomain)
                .toList();
    }

    @Override
    public void delete(TemplateId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTemplate(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTemplate from(Template aggregate) {
            return new StoredTemplate(
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

        Template toDomain() {
            return Template.rehydrate(
                    TemplateId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TemplateStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
