package et.gov.ethgov.citizen.domain.citizen;

import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record CitizenRegistered(
        CitizenId citizenId, TenantId registeringTenantId, EthiopianPhoneNumber phone, Instant occurredAt)
        implements CitizenDomainEvent {}
