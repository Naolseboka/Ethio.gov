package et.gov.ethgov.notification.domain.message;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class OutboundNotification {
    private static final int MAX_BODY = 4000;
    private static final int MAX_SUBJECT = 200;

    private final NotificationId id;
    private final TenantId tenantId;
    private final NotificationNumber notificationNumber;
    private final NotificationChannel channel;
    private NotificationStatus status;
    private NotificationRecipient recipient;
    private NotificationLocale locale;
    private String subject;
    private String body;
    private String templateCode;
    private String citizenId;
    private String organizationId;
    private String sourceReference;
    private String providerReference;
    private String failureReason;
    private String cancelReason;
    private Instant scheduledAt;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant failedAt;
    private Instant cancelledAt;
    private Instant updatedAt;
    private long version;
    private final List<NotificationDomainEvent> domainEvents = new ArrayList<>();

    private OutboundNotification(
            NotificationId id,
            TenantId tenantId,
            NotificationNumber notificationNumber,
            NotificationChannel channel,
            NotificationStatus status,
            NotificationRecipient recipient,
            NotificationLocale locale,
            String subject,
            String body,
            String templateCode,
            String citizenId,
            String organizationId,
            String sourceReference,
            String providerReference,
            String failureReason,
            String cancelReason,
            Instant scheduledAt,
            Instant sentAt,
            Instant deliveredAt,
            Instant failedAt,
            Instant cancelledAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.notificationNumber = notificationNumber;
        this.channel = channel;
        this.status = status;
        this.recipient = recipient;
        this.locale = locale;
        this.subject = subject;
        this.body = body;
        this.templateCode = templateCode;
        this.citizenId = citizenId;
        this.organizationId = organizationId;
        this.sourceReference = sourceReference;
        this.providerReference = providerReference;
        this.failureReason = failureReason;
        this.cancelReason = cancelReason;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
        this.failedAt = failedAt;
        this.cancelledAt = cancelledAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static OutboundNotification schedule(
            TenantId tenantId,
            NotificationNumber notificationNumber,
            NotificationChannel channel,
            String phone,
            String email,
            String deviceToken,
            NotificationLocale locale,
            String subject,
            String body,
            String templateCode,
            String citizenId,
            String organizationId,
            String sourceReference,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(notificationNumber, "notificationNumber");
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(now, "now");
        requireNonBlank(body, "body");
        if (body.trim().length() > MAX_BODY) {
            throw new IllegalArgumentException("body length must be <= " + MAX_BODY);
        }
        String normalizedSubject = blankToNull(subject);
        if (channel == NotificationChannel.EMAIL) {
            requireNonBlank(normalizedSubject, "subject");
        }
        if (normalizedSubject != null && normalizedSubject.length() > MAX_SUBJECT) {
            throw new IllegalArgumentException("subject length must be <= " + MAX_SUBJECT);
        }
        NotificationRecipient recipient =
                NotificationRecipient.forChannel(channel, phone, email, deviceToken);
        validateOptionalId(citizenId, "cit_", "citizenId");
        validateOptionalId(organizationId, "org_", "organizationId");
        String source = blankToNull(sourceReference);
        if (source != null && source.length() > 64) {
            throw new IllegalArgumentException("sourceReference length must be <= 64");
        }
        OutboundNotification notification = new OutboundNotification(
                NotificationId.generate(),
                tenantId,
                notificationNumber,
                channel,
                NotificationStatus.PENDING,
                recipient,
                locale,
                normalizedSubject,
                body.trim(),
                blankToNull(templateCode),
                blankToNull(citizenId),
                blankToNull(organizationId),
                source,
                null,
                null,
                null,
                now,
                null,
                null,
                null,
                null,
                now,
                0L);
        notification.record(new NotificationScheduled(
                notification.id, tenantId, notificationNumber, channel, now));
        return notification;
    }

    public static OutboundNotification rehydrate(
            NotificationId id,
            TenantId tenantId,
            NotificationNumber notificationNumber,
            NotificationChannel channel,
            NotificationStatus status,
            NotificationRecipient recipient,
            NotificationLocale locale,
            String subject,
            String body,
            String templateCode,
            String citizenId,
            String organizationId,
            String sourceReference,
            String providerReference,
            String failureReason,
            String cancelReason,
            Instant scheduledAt,
            Instant sentAt,
            Instant deliveredAt,
            Instant failedAt,
            Instant cancelledAt,
            Instant updatedAt,
            long version) {
        return new OutboundNotification(
                id, tenantId, notificationNumber, channel, status, recipient, locale, subject, body,
                templateCode, citizenId, organizationId, sourceReference, providerReference,
                failureReason, cancelReason, scheduledAt, sentAt, deliveredAt, failedAt, cancelledAt,
                updatedAt, version);
    }

    public void markSent(String providerReference, Instant now) {
        Objects.requireNonNull(now, "now");
        requireNonBlank(providerReference, "providerReference");
        if (providerReference.trim().length() < 5 || providerReference.trim().length() > 120) {
            throw new IllegalArgumentException("providerReference length must be between 5 and 120");
        }
        if (status != NotificationStatus.PENDING) {
            throw invalidState("mark sent");
        }
        this.status = NotificationStatus.SENT;
        this.providerReference = providerReference.trim();
        this.sentAt = now;
        this.updatedAt = now;
        record(new NotificationSent(id, this.providerReference, now));
    }

    public void markDelivered(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != NotificationStatus.SENT) {
            throw invalidState("mark delivered");
        }
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = now;
        this.updatedAt = now;
        record(new NotificationDelivered(id, now));
    }

    public void markFailed(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != NotificationStatus.PENDING && status != NotificationStatus.SENT) {
            throw invalidState("mark failed");
        }
        this.status = NotificationStatus.FAILED;
        this.failureReason = reason.trim();
        this.failedAt = now;
        this.updatedAt = now;
        record(new NotificationFailed(id, this.failureReason, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == NotificationStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != NotificationStatus.PENDING) {
            throw invalidState("cancel");
        }
        this.status = NotificationStatus.CANCELLED;
        this.cancelReason = reason.trim();
        this.cancelledAt = now;
        this.updatedAt = now;
        record(new NotificationCancelled(id, this.cancelReason, now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_NOTIFICATION_STATE",
                "Invalid Notification State",
                "Cannot " + action + " notification in status " + status,
                409);
    }

    private static void requireReason(String reason) {
        requireNonBlank(reason, "reason");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
        }
    }

    private static void validateOptionalId(String value, String prefix, String field) {
        if (value == null || value.isBlank()) return;
        String n = value.trim();
        if (!n.startsWith(prefix) || n.length() < 40) {
            throw new IllegalArgumentException(field + " must be a valid " + prefix + " identifier");
        }
    }

    private static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void record(NotificationDomainEvent event) {
        domainEvents.add(event);
    }

    public List<NotificationDomainEvent> pullDomainEvents() {
        List<NotificationDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public NotificationId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public NotificationNumber notificationNumber() { return notificationNumber; }
    public NotificationChannel channel() { return channel; }
    public NotificationStatus status() { return status; }
    public NotificationRecipient recipient() { return recipient; }
    public NotificationLocale locale() { return locale; }
    public Optional<String> subject() { return Optional.ofNullable(subject); }
    public String body() { return body; }
    public Optional<String> templateCode() { return Optional.ofNullable(templateCode); }
    public Optional<String> citizenId() { return Optional.ofNullable(citizenId); }
    public Optional<String> organizationId() { return Optional.ofNullable(organizationId); }
    public Optional<String> sourceReference() { return Optional.ofNullable(sourceReference); }
    public Optional<String> providerReference() { return Optional.ofNullable(providerReference); }
    public Optional<String> failureReason() { return Optional.ofNullable(failureReason); }
    public Optional<String> cancelReason() { return Optional.ofNullable(cancelReason); }
    public Instant scheduledAt() { return scheduledAt; }
    public Optional<Instant> sentAt() { return Optional.ofNullable(sentAt); }
    public Optional<Instant> deliveredAt() { return Optional.ofNullable(deliveredAt); }
    public Optional<Instant> failedAt() { return Optional.ofNullable(failedAt); }
    public Optional<Instant> cancelledAt() { return Optional.ofNullable(cancelledAt); }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
