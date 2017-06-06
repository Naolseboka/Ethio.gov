package et.gov.ethgov.platform.persistence.entity;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base persistence fields shared by tenant-scoped aggregates.
 */
@MappedSuperclass
public abstract class AbstractTenantEntity {

    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false, updatable = false, length = 40)
    private String tenantId;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 128)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 128)
    private String updatedBy;

    protected AbstractTenantEntity() {}

    protected void initialize(TenantId tenantId, String actor) {
        this.id = UUID.randomUUID().toString();
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId").value();
        this.createdBy = Objects.requireNonNull(actor, "actor");
        this.updatedBy = actor;
    }

    @PrePersist
    void onPersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public TenantId getTenantId() {
        return TenantId.of(tenantId);
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    protected void markUpdatedBy(String actor) {
        this.updatedBy = Objects.requireNonNull(actor, "actor");
    }
}
