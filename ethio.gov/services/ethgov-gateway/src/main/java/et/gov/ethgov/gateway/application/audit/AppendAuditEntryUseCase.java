package et.gov.ethgov.gateway.application.audit;

import et.gov.ethgov.gateway.application.GatewayEventPublisher;
import et.gov.ethgov.gateway.domain.audit.AuditEntry;
import et.gov.ethgov.gateway.domain.audit.AuditEntryId;
import et.gov.ethgov.gateway.domain.audit.AuditEntryRepository;
import et.gov.ethgov.gateway.domain.audit.AuditOutcome;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppendAuditEntryUseCase {
    private final AuditEntryRepository audits;
    private final GatewayEventPublisher eventPublisher;
    private final Clock clock;

    public AppendAuditEntryUseCase(
            AuditEntryRepository audits, GatewayEventPublisher eventPublisher, Clock clock) {
        this.audits = audits;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public AuditEntryId execute(Command command) {
        Objects.requireNonNull(command, "command");
        AuditEntry entry = AuditEntry.append(
                TenantId.of(command.tenantId()),
                command.actorId(),
                command.action(),
                command.resourceType(),
                command.resourceId(),
                AuditOutcome.valueOf(command.outcome().trim().toUpperCase()),
                command.detailJson(),
                clock.instant());
        audits.append(entry);
        eventPublisher.publishAuditEvents(entry.pullDomainEvents());
        return entry.id();
    }

    public record Command(
            String tenantId,
            String actorId,
            String action,
            String resourceType,
            String resourceId,
            String outcome,
            String detailJson) {}
}
