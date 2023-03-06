package et.gov.ethgov.opendata.application.dataset;

import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.opendata.domain.dataset.DatasetNumber;
import et.gov.ethgov.opendata.domain.dataset.DatasetRepository;
import et.gov.ethgov.opendata.domain.dataset.DatasetSlug;
import et.gov.ethgov.opendata.domain.dataset.OpenDataset;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDatasetUseCase {

    private final DatasetRepository datasets;

    public GetDatasetUseCase(DatasetRepository datasets) {
        this.datasets = datasets;
    }

    @Transactional(readOnly = true)
    public OpenDataset byId(String datasetId) {
        return datasets
                .findById(DatasetId.of(datasetId))
                .orElseThrow(() -> new DomainException(
                        "DATASET_NOT_FOUND", "Dataset Not Found", "Dataset does not exist", 404));
    }

    @Transactional(readOnly = true)
    public OpenDataset byNumber(String tenantId, String datasetNumber) {
        return datasets
                .findByTenantAndDatasetNumber(TenantId.of(tenantId), DatasetNumber.of(datasetNumber))
                .orElseThrow(() -> new DomainException(
                        "DATASET_NOT_FOUND", "Dataset Not Found", "Dataset does not exist", 404));
    }

    @Transactional(readOnly = true)
    public OpenDataset bySlug(String tenantId, String slug) {
        return datasets
                .findByTenantAndSlug(TenantId.of(tenantId), DatasetSlug.of(slug))
                .orElseThrow(() -> new DomainException(
                        "DATASET_NOT_FOUND", "Dataset Not Found", "Dataset does not exist", 404));
    }
}
