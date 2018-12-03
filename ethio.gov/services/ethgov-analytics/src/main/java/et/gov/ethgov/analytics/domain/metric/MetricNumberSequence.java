package et.gov.ethgov.analytics.domain.metric;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface MetricNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
