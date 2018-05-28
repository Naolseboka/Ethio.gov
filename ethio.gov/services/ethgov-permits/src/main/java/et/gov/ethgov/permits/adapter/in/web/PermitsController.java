package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.permit.ActivatePermitUseCase;
import et.gov.ethgov.permits.application.permit.ApplyPermitUseCase;
import et.gov.ethgov.permits.application.permit.AttachPermitDocumentUseCase;
import et.gov.ethgov.permits.application.permit.CancelPermitUseCase;
import et.gov.ethgov.permits.application.permit.CompletePermitUseCase;
import et.gov.ethgov.permits.application.permit.ExpirePermitUseCase;
import et.gov.ethgov.permits.application.permit.GetPermitUseCase;
import et.gov.ethgov.permits.application.permit.IssuePermitUseCase;
import et.gov.ethgov.permits.application.permit.LinkPermitCaseUseCase;
import et.gov.ethgov.permits.application.permit.LinkPermitWorkflowUseCase;
import et.gov.ethgov.permits.application.permit.RejectPermitUseCase;
import et.gov.ethgov.permits.application.permit.ReinstatePermitUseCase;
import et.gov.ethgov.permits.application.permit.RevokePermitUseCase;
import et.gov.ethgov.permits.application.permit.StartReviewUseCase;
import et.gov.ethgov.permits.application.permit.SuspendPermitUseCase;
import et.gov.ethgov.permits.application.permit.UpdatePermitUseCase;
import et.gov.ethgov.permits.domain.permit.ConstructionPermit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/v1/permits")
public class PermitsController {

    private final ApplyPermitUseCase applyPermit;
    private final UpdatePermitUseCase updatePermit;
    private final StartReviewUseCase startReview;
    private final IssuePermitUseCase issuePermit;
    private final ActivatePermitUseCase activatePermit;
    private final SuspendPermitUseCase suspendPermit;
    private final ReinstatePermitUseCase reinstatePermit;
    private final CompletePermitUseCase completePermit;
    private final RevokePermitUseCase revokePermit;
    private final RejectPermitUseCase rejectPermit;
    private final ExpirePermitUseCase expirePermit;
    private final AttachPermitDocumentUseCase attachDocument;
    private final LinkPermitCaseUseCase linkCase;
    private final LinkPermitWorkflowUseCase linkWorkflow;
    private final CancelPermitUseCase cancelPermit;
    private final GetPermitUseCase getPermit;

    public PermitsController(
            ApplyPermitUseCase applyPermit,
            UpdatePermitUseCase updatePermit,
            StartReviewUseCase startReview,
            IssuePermitUseCase issuePermit,
            ActivatePermitUseCase activatePermit,
            SuspendPermitUseCase suspendPermit,
            ReinstatePermitUseCase reinstatePermit,
            CompletePermitUseCase completePermit,
            RevokePermitUseCase revokePermit,
            RejectPermitUseCase rejectPermit,
            ExpirePermitUseCase expirePermit,
            AttachPermitDocumentUseCase attachDocument,
            LinkPermitCaseUseCase linkCase,
            LinkPermitWorkflowUseCase linkWorkflow,
            CancelPermitUseCase cancelPermit,
            GetPermitUseCase getPermit) {
        this.applyPermit = applyPermit;
        this.updatePermit = updatePermit;
        this.startReview = startReview;
        this.issuePermit = issuePermit;
        this.activatePermit = activatePermit;
        this.suspendPermit = suspendPermit;
        this.reinstatePermit = reinstatePermit;
        this.completePermit = completePermit;
        this.revokePermit = revokePermit;
        this.rejectPermit = rejectPermit;
        this.expirePermit = expirePermit;
        this.attachDocument = attachDocument;
        this.linkCase = linkCase;
        this.linkWorkflow = linkWorkflow;
        this.cancelPermit = cancelPermit;
        this.getPermit = getPermit;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermitIdResponse apply(@Valid @RequestBody ApplyPermitRequest request) {
        return new PermitIdResponse(applyPermit.execute(new ApplyPermitUseCase.Command(
                        request.tenantId(),
                        request.type(),
                        request.structureUse(),
                        request.projectName(),
                        request.description(),
                        request.citizenId(),
                        request.organizationId(),
                        request.parcelId(),
                        request.floors(),
                        request.floorAreaSquareMeters(),
                        request.estimatedCostEtb(),
                        request.region(),
                        request.zone(),
                        request.woreda(),
                        request.kebele(),
                        request.locality(),
                        request.linkedCaseId()))
                .value());
    }

    @GetMapping("/lookup")
    public PermitResponse lookup(@RequestParam String tenantId, @RequestParam String permitNumber) {
        return PermitResponse.from(getPermit.byNumber(tenantId, permitNumber));
    }

    @GetMapping("/{permitId}")
    public PermitResponse get(@PathVariable String permitId) {
        return PermitResponse.from(getPermit.byId(permitId));
    }

    @PutMapping("/{permitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String permitId, @Valid @RequestBody UpdatePermitRequest request) {
        updatePermit.execute(new UpdatePermitUseCase.Command(
                permitId,
                request.projectName(),
                request.description(),
                request.structureUse(),
                request.floors(),
                request.floorAreaSquareMeters(),
                request.estimatedCostEtb(),
                request.region(),
                request.zone(),
                request.woreda(),
                request.kebele(),
                request.locality()));
    }

    @PostMapping("/{permitId}/review")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startReview(@PathVariable String permitId) {
        startReview.execute(permitId);
    }

    @PostMapping("/{permitId}/issue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issue(@PathVariable String permitId, @Valid @RequestBody IssueRequest request) {
        issuePermit.execute(permitId, request.validFrom(), request.validTo());
    }

    @PostMapping("/{permitId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable String permitId) {
        activatePermit.execute(permitId);
    }

    @PostMapping("/{permitId}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable String permitId, @Valid @RequestBody ReasonRequest request) {
        suspendPermit.execute(permitId, request.reason());
    }

    @PostMapping("/{permitId}/reinstate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reinstate(@PathVariable String permitId) {
        reinstatePermit.execute(permitId);
    }

    @PostMapping("/{permitId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable String permitId) {
        completePermit.execute(permitId);
    }

    @PostMapping("/{permitId}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable String permitId, @Valid @RequestBody ReasonRequest request) {
        revokePermit.execute(permitId, request.reason());
    }

    @PostMapping("/{permitId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable String permitId, @Valid @RequestBody ReasonRequest request) {
        rejectPermit.execute(permitId, request.reason());
    }

    @PostMapping("/{permitId}/expire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void expire(@PathVariable String permitId) {
        expirePermit.execute(permitId);
    }

    @PostMapping("/{permitId}/documents")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attachDocument(@PathVariable String permitId, @Valid @RequestBody DocumentRequest request) {
        attachDocument.execute(permitId, request.documentId());
    }

    @PostMapping("/{permitId}/case")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkCase(@PathVariable String permitId, @Valid @RequestBody CaseRequest request) {
        linkCase.execute(permitId, request.caseId());
    }

    @PostMapping("/{permitId}/workflow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkWorkflow(@PathVariable String permitId, @Valid @RequestBody WorkflowRequest request) {
        linkWorkflow.execute(permitId, request.processInstanceId());
    }

    @PostMapping("/{permitId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String permitId, @Valid @RequestBody ReasonRequest request) {
        cancelPermit.execute(permitId, request.reason());
    }

    public record ApplyPermitRequest(
            @NotBlank String tenantId,
            @NotBlank String type,
            @NotBlank String structureUse,
            @NotBlank String projectName,
            String description,
            String citizenId,
            String organizationId,
            @NotBlank String parcelId,
            @Min(1) @Max(200) int floors,
            @NotBlank String floorAreaSquareMeters,
            String estimatedCostEtb,
            @NotBlank String region,
            String zone,
            @NotBlank String woreda,
            String kebele,
            String locality,
            String linkedCaseId) {}

    public record UpdatePermitRequest(
            @NotBlank String projectName,
            String description,
            @NotBlank String structureUse,
            @Min(1) @Max(200) int floors,
            @NotBlank String floorAreaSquareMeters,
            String estimatedCostEtb,
            @NotBlank String region,
            String zone,
            @NotBlank String woreda,
            String kebele,
            String locality) {}

    public record IssueRequest(@NotBlank String validFrom, @NotBlank String validTo) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record DocumentRequest(@NotBlank String documentId) {}

    public record CaseRequest(@NotBlank String caseId) {}

    public record WorkflowRequest(@NotBlank String processInstanceId) {}

    public record PermitIdResponse(String permitId) {}

    public record PermitResponse(
            String permitId,
            String tenantId,
            String permitNumber,
            String type,
            String structureUse,
            String projectName,
            String description,
            String status,
            String citizenId,
            String organizationId,
            String parcelId,
            int floors,
            String floorAreaSquareMeters,
            String estimatedCostEtb,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String linkedCaseId,
            String processInstanceId,
            Set<String> documentIds,
            String validFrom,
            String validTo,
            String issuedAt,
            String completedAt) {

        static PermitResponse from(ConstructionPermit permit) {
            return new PermitResponse(
                    permit.id().value(),
                    permit.tenantId().value(),
                    permit.permitNumber().value(),
                    permit.type().name(),
                    permit.structureUse().name(),
                    permit.projectName(),
                    permit.description().orElse(null),
                    permit.status().name(),
                    permit.applicant().citizenId().orElse(null),
                    permit.applicant().organizationId().orElse(null),
                    permit.parcelId(),
                    permit.floors(),
                    permit.floorAreaSquareMeters().toString(),
                    permit.estimatedCostEtb().map(m -> m.amount().toPlainString()).orElse(null),
                    permit.region(),
                    permit.zone().orElse(null),
                    permit.woreda(),
                    permit.kebele().orElse(null),
                    permit.locality().orElse(null),
                    permit.linkedCaseId().orElse(null),
                    permit.processInstanceId().orElse(null),
                    permit.documentIds(),
                    permit.validFrom().map(java.time.Instant::toString).orElse(null),
                    permit.validTo().map(java.time.Instant::toString).orElse(null),
                    permit.issuedAt().map(java.time.Instant::toString).orElse(null),
                    permit.completedAt().map(java.time.Instant::toString).orElse(null));
        }
    }
}
