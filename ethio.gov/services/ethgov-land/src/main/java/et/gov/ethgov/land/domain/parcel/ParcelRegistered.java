package et.gov.ethgov.land.domain.parcel;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record ParcelRegistered(
        ParcelId parcelId, TenantId tenantId, ParcelNumber parcelNumber, LandUse landUse, Instant occurredAt)
        implements ParcelDomainEvent {}
