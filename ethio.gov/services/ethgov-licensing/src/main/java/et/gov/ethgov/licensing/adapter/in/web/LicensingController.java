package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.license.ActivateLicenseUseCase;
import et.gov.ethgov.licensing.application.license.ApplyLicenseUseCase;
import et.gov.ethgov.licensing.application.license.AttachLicenseDocumentUseCase;
import et.gov.ethgov.licensing.application.license.CancelLicenseUseCase;
import et.gov.ethgov.licensing.application.license.ExpireLicenseUseCase;
import et.gov.ethgov.licensing.application.license.GetLicenseUseCase;
import et.gov.ethgov.licensing.application.license.IssueLicenseUseCase;
import et.gov.ethgov.licensing.application.license.LinkLicenseCaseUseCase;
import et.gov.ethgov.licensing.application.license.LinkLicenseWorkflowUseCase;
import et.gov.ethgov.licensing.application.license.RejectLicenseUseCase;
import et.gov.ethgov.licensing.application.license.ReinstateLicenseUseCase;
import et.gov.ethgov.licensing.application.license.RenewLicenseUseCase;
import et.gov.ethgov.licensing.application.license.RevokeLicenseUseCase;
import et.gov.ethgov.licensing.application.license.StartReviewUseCase;
import et.gov.ethgov.licensing.application.license.SuspendLicenseUseCase;
import et.gov.ethgov.licensing.application.license.UpdateLicenseUseCase;
import et.gov.ethgov.licensing.domain.license.BusinessLicense;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/licenses")
public class LicensingController {

    private final ApplyLicenseUseCase applyLicense;
    private final UpdateLicenseUseCase updateLicense;
    private final StartReviewUseCase startReview;
    private final IssueLicenseUseCase issueLicense;
    private final ActivateLicenseUseCase activateLicense;
    private final SuspendLicenseUseCase suspendLicense;
    private final ReinstateLicenseUseCase reinstateLicense;
    private final RevokeLicenseUseCase revokeLicense;
    private final RejectLicenseUseCase rejectLicense;
    private final RenewLicenseUseCase renewLicense;
    private final ExpireLicenseUseCase expireLicense;
    private final AttachLicenseDocumentUseCase attachDocument;
    private final LinkLicenseCaseUseCase linkCase;
    private final LinkLicenseWorkflowUseCase linkWorkflow;
    private final CancelLicenseUseCase cancelLicense;
    private final GetLicenseUseCase getLicense;

    public LicensingController(
            ApplyLicenseUseCase applyLicense,
            UpdateLicenseUseCase updateLicense,
            StartReviewUseCase startReview,
            IssueLicenseUseCase issueLicense,
            ActivateLicenseUseCase activateLicense,
            SuspendLicenseUseCase suspendLicense,
            ReinstateLicenseUseCase reinstateLicense,
            RevokeLicenseUseCase revokeLicense,
            RejectLicenseUseCase rejectLicense,
            RenewLicenseUseCase renewLicense,
            ExpireLicenseUseCase expireLicense,
            AttachLicenseDocumentUseCase attachDocument,
            LinkLicenseCaseUseCase linkCase,
            LinkLicenseWorkflowUseCase linkWorkflow,
            CancelLicenseUseCase cancelLicense,
            GetLicenseUseCase getLicense) {
        this.applyLicense = applyLicense;
        this.updateLicense = updateLicense;
        this.startReview = startReview;
        this.issueLicense = issueLicense;
        this.activateLicense = activateLicense;
        this.suspendLicense = suspendLicense;
        this.reinstateLicense = reinstateLicense;
        this.revokeLicense = revokeLicense;
        this.rejectLicense = rejectLicense;
        this.renewLicense = renewLicense;
        this.expireLicense = expireLicense;
        this.attachDocument = attachDocument;
        this.linkCase = linkCase;
        this.linkWorkflow = linkWorkflow;
        this.cancelLicense = cancelLicense;
        this.getLicense = getLicense;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LicenseIdResponse apply(@Valid @RequestBody ApplyLicenseRequest request) {
        return new LicenseIdResponse(applyLicense.execute(new ApplyLicenseUseCase.Command(
                        request.tenantId(),
                        request.type(),
                        request.businessName(),
                        request.activityDescription(),
                        request.organizationId(),
                        request.citizenId(),
                        request.region(),
                        request.zone(),
                        request.woreda(),
                        request.kebele(),
                        request.locality(),
                        request.parcelId(),
                        request.linkedCaseId()))
                .value());
    }

    @GetMapping("/lookup")
    public LicenseResponse lookup(@RequestParam String tenantId, @RequestParam String licenseNumber) {
        return LicenseResponse.from(getLicense.byNumber(tenantId, licenseNumber));
    }

    @GetMapping("/{licenseId}")
    public LicenseResponse get(@PathVariable String licenseId) {
        return LicenseResponse.from(getLicense.byId(licenseId));
    }

    @PutMapping("/{licenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String licenseId, @Valid @RequestBody UpdateLicenseRequest request) {
        updateLicense.execute(new UpdateLicenseUseCase.Command(
                licenseId,
                request.businessName(),
                request.activityDescription(),
                request.region(),
                request.zone(),
                request.woreda(),
                request.kebele(),
                request.locality(),
                request.parcelId()));
    }

    @PostMapping("/{licenseId}/review")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startReview(@PathVariable String licenseId) {
        startReview.execute(licenseId);
    }

    @PostMapping("/{licenseId}/issue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issue(@PathVariable String licenseId, @Valid @RequestBody IssueRequest request) {
        issueLicense.execute(licenseId, request.validFrom(), request.validTo());
    }

    @PostMapping("/{licenseId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable String licenseId) {
        activateLicense.execute(licenseId);
    }

    @PostMapping("/{licenseId}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable String licenseId, @Valid @RequestBody ReasonRequest request) {
        suspendLicense.execute(licenseId, request.reason());
    }

    @PostMapping("/{licenseId}/reinstate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reinstate(@PathVariable String licenseId) {
        reinstateLicense.execute(licenseId);
    }

    @PostMapping("/{licenseId}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable String licenseId, @Valid @RequestBody ReasonRequest request) {
        revokeLicense.execute(licenseId, request.reason());
    }

    @PostMapping("/{licenseId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable String licenseId, @Valid @RequestBody ReasonRequest request) {
        rejectLicense.execute(licenseId, request.reason());
    }

    @PostMapping("/{licenseId}/renew")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void renew(@PathVariable String licenseId, @Valid @RequestBody RenewRequest request) {
        renewLicense.execute(licenseId, request.validTo());
    }

    @PostMapping("/{licenseId}/expire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void expire(@PathVariable String licenseId) {
        expireLicense.execute(licenseId);
    }

    @PostMapping("/{licenseId}/documents")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attachDocument(@PathVariable String licenseId, @Valid @RequestBody DocumentRequest request) {
        attachDocument.execute(licenseId, request.documentId());
    }

    @PostMapping("/{licenseId}/case")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkCase(@PathVariable String licenseId, @Valid @RequestBody CaseRequest request) {
        linkCase.execute(licenseId, request.caseId());
    }

    @PostMapping("/{licenseId}/workflow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkWorkflow(@PathVariable String licenseId, @Valid @RequestBody WorkflowRequest request) {
        linkWorkflow.execute(licenseId, request.processInstanceId());
    }

    @PostMapping("/{licenseId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String licenseId, @Valid @RequestBody ReasonRequest request) {
        cancelLicense.execute(licenseId, request.reason());
    }

    public record ApplyLicenseRequest(
            @NotBlank String tenantId,
            @NotBlank String type,
            @NotBlank String businessName,
            String activityDescription,
            @NotBlank String organizationId,
            String citizenId,
            @NotBlank String region,
            String zone,
            @NotBlank String woreda,
            String kebele,
            String locality,
            String parcelId,
            String linkedCaseId) {}

    public record UpdateLicenseRequest(
            @NotBlank String businessName,
            String activityDescription,
            @NotBlank String region,
            String zone,
            @NotBlank String woreda,
            String kebele,
            String locality,
            String parcelId) {}

    public record IssueRequest(@NotBlank String validFrom, @NotBlank String validTo) {}

    public record RenewRequest(@NotBlank String validTo) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record DocumentRequest(@NotBlank String documentId) {}

    public record CaseRequest(@NotBlank String caseId) {}

    public record WorkflowRequest(@NotBlank String processInstanceId) {}

    public record LicenseIdResponse(String licenseId) {}

    public record LicenseResponse(
            String licenseId,
            String tenantId,
            String licenseNumber,
            String type,
            String businessName,
            String activityDescription,
            String status,
            String organizationId,
            String citizenId,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String parcelId,
            String linkedCaseId,
            String processInstanceId,
            Set<String> documentIds,
            String validFrom,
            String validTo,
            String issuedAt) {

        static LicenseResponse from(BusinessLicense license) {
            return new LicenseResponse(
                    license.id().value(),
                    license.tenantId().value(),
                    license.licenseNumber().value(),
                    license.type().name(),
                    license.businessName(),
                    license.activityDescription().orElse(null),
                    license.status().name(),
                    license.holder().organizationId(),
                    license.holder().citizenId().orElse(null),
                    license.region(),
                    license.zone().orElse(null),
                    license.woreda(),
                    license.kebele().orElse(null),
                    license.locality().orElse(null),
                    license.parcelId().orElse(null),
                    license.linkedCaseId().orElse(null),
                    license.processInstanceId().orElse(null),
                    license.documentIds(),
                    license.validFrom().map(java.time.Instant::toString).orElse(null),
                    license.validTo().map(java.time.Instant::toString).orElse(null),
                    license.issuedAt().map(java.time.Instant::toString).orElse(null));
        }
    }
}
