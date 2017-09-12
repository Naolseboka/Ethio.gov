package et.gov.ethgov.casemgmt.adapter.in.web;

import et.gov.ethgov.casemgmt.application.casefile.AddCaseNoteUseCase;
import et.gov.ethgov.casemgmt.application.casefile.AssignCaseUseCase;
import et.gov.ethgov.casemgmt.application.casefile.AttachCaseDocumentUseCase;
import et.gov.ethgov.casemgmt.application.casefile.CancelCaseUseCase;
import et.gov.ethgov.casemgmt.application.casefile.ChangeCaseStatusUseCase;
import et.gov.ethgov.casemgmt.application.casefile.GetCaseUseCase;
import et.gov.ethgov.casemgmt.application.casefile.LinkCaseWorkflowUseCase;
import et.gov.ethgov.casemgmt.application.casefile.OpenCaseUseCase;
import et.gov.ethgov.casemgmt.application.casefile.UpdateCaseUseCase;
import et.gov.ethgov.casemgmt.domain.casefile.CaseNote;
import et.gov.ethgov.casemgmt.domain.casefile.GovernmentCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
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
@RequestMapping("/v1/cases")
public class CaseController {

    private final OpenCaseUseCase openCase;
    private final UpdateCaseUseCase updateCase;
    private final AssignCaseUseCase assignCase;
    private final ChangeCaseStatusUseCase changeStatus;
    private final LinkCaseWorkflowUseCase linkWorkflow;
    private final AttachCaseDocumentUseCase attachDocument;
    private final AddCaseNoteUseCase addNote;
    private final CancelCaseUseCase cancelCase;
    private final GetCaseUseCase getCase;

    public CaseController(
            OpenCaseUseCase openCase,
            UpdateCaseUseCase updateCase,
            AssignCaseUseCase assignCase,
            ChangeCaseStatusUseCase changeStatus,
            LinkCaseWorkflowUseCase linkWorkflow,
            AttachCaseDocumentUseCase attachDocument,
            AddCaseNoteUseCase addNote,
            CancelCaseUseCase cancelCase,
            GetCaseUseCase getCase) {
        this.openCase = openCase;
        this.updateCase = updateCase;
        this.assignCase = assignCase;
        this.changeStatus = changeStatus;
        this.linkWorkflow = linkWorkflow;
        this.attachDocument = attachDocument;
        this.addNote = addNote;
        this.cancelCase = cancelCase;
        this.getCase = getCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseIdResponse open(@Valid @RequestBody OpenCaseRequest request) {
        return new CaseIdResponse(openCase.execute(new OpenCaseUseCase.Command(
                        request.tenantId(),
                        request.type(),
                        request.title(),
                        request.description(),
                        request.priority(),
                        request.citizenId(),
                        request.organizationId(),
                        request.owningUnitId()))
                .value());
    }

    @GetMapping("/lookup")
    public CaseResponse lookup(@RequestParam String tenantId, @RequestParam String caseNumber) {
        return CaseResponse.from(getCase.byNumber(tenantId, caseNumber));
    }

    @GetMapping("/{caseId}")
    public CaseResponse get(@PathVariable String caseId) {
        return CaseResponse.from(getCase.byId(caseId));
    }

    @PutMapping("/{caseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String caseId, @Valid @RequestBody UpdateCaseRequest request) {
        updateCase.execute(new UpdateCaseUseCase.Command(
                caseId, request.title(), request.description(), request.priority()));
    }

    @PostMapping("/{caseId}/assign")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assign(@PathVariable String caseId, @Valid @RequestBody AssignRequest request) {
        assignCase.execute(caseId, request.assigneeAccountId());
    }

    @PostMapping("/{caseId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void status(@PathVariable String caseId, @Valid @RequestBody StatusRequest request) {
        changeStatus.execute(caseId, request.status());
    }

    @PostMapping("/{caseId}/workflow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkWorkflow(@PathVariable String caseId, @Valid @RequestBody WorkflowRequest request) {
        linkWorkflow.execute(caseId, request.processInstanceId());
    }

    @PostMapping("/{caseId}/documents")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attachDocument(@PathVariable String caseId, @Valid @RequestBody DocumentRequest request) {
        attachDocument.execute(caseId, request.documentId());
    }

    @PostMapping("/{caseId}/notes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addNote(@PathVariable String caseId, @Valid @RequestBody NoteRequest request) {
        addNote.execute(caseId, request.authorAccountId(), request.body());
    }

    @PostMapping("/{caseId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String caseId, @Valid @RequestBody CancelRequest request) {
        cancelCase.execute(caseId, request.reason());
    }

    public record OpenCaseRequest(
            @NotBlank String tenantId,
            @NotBlank String type,
            @NotBlank String title,
            String description,
            @NotBlank String priority,
            String citizenId,
            String organizationId,
            String owningUnitId) {}

    public record UpdateCaseRequest(
            @NotBlank String title, String description, @NotBlank String priority) {}

    public record AssignRequest(@NotBlank String assigneeAccountId) {}

    public record StatusRequest(@NotBlank String status) {}

    public record WorkflowRequest(@NotBlank String processInstanceId) {}

    public record DocumentRequest(@NotBlank String documentId) {}

    public record NoteRequest(@NotBlank String authorAccountId, @NotBlank @Size(min = 2, max = 2000) String body) {}

    public record CancelRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record CaseIdResponse(String caseId) {}

    public record CaseResponse(
            String caseId,
            String tenantId,
            String caseNumber,
            String type,
            String title,
            String description,
            String status,
            String priority,
            String citizenId,
            String organizationId,
            String assigneeAccountId,
            String owningUnitId,
            String processInstanceId,
            Set<String> documentIds,
            List<NoteResponse> notes) {

        static CaseResponse from(GovernmentCase governmentCase) {
            return new CaseResponse(
                    governmentCase.id().value(),
                    governmentCase.tenantId().value(),
                    governmentCase.caseNumber().value(),
                    governmentCase.type().name(),
                    governmentCase.title(),
                    governmentCase.description().orElse(null),
                    governmentCase.status().name(),
                    governmentCase.priority().name(),
                    governmentCase.party().citizenId().orElse(null),
                    governmentCase.party().organizationId().orElse(null),
                    governmentCase.assigneeAccountId().orElse(null),
                    governmentCase.owningUnitId().orElse(null),
                    governmentCase.processInstanceId().orElse(null),
                    governmentCase.documentIds(),
                    governmentCase.notes().stream().map(NoteResponse::from).toList());
        }
    }

    public record NoteResponse(String noteId, String authorAccountId, String body, String createdAt) {
        static NoteResponse from(CaseNote note) {
            return new NoteResponse(
                    note.noteId(), note.authorAccountId(), note.body(), note.createdAt().toString());
        }
    }
}
