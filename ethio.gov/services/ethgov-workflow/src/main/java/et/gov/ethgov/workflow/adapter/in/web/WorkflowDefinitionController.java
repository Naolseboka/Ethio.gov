package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.definition.CreateWorkflowDefinitionUseCase;
import et.gov.ethgov.workflow.application.definition.GetWorkflowDefinitionUseCase;
import et.gov.ethgov.workflow.application.definition.PublishWorkflowDefinitionUseCase;
import et.gov.ethgov.workflow.application.definition.RetireWorkflowDefinitionUseCase;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinition;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionId;
import et.gov.ethgov.workflow.domain.definition.WorkflowStep;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/workflow-definitions")
public class WorkflowDefinitionController {

    private final CreateWorkflowDefinitionUseCase createDefinition;
    private final PublishWorkflowDefinitionUseCase publishDefinition;
    private final RetireWorkflowDefinitionUseCase retireDefinition;
    private final GetWorkflowDefinitionUseCase getDefinition;

    public WorkflowDefinitionController(
            CreateWorkflowDefinitionUseCase createDefinition,
            PublishWorkflowDefinitionUseCase publishDefinition,
            RetireWorkflowDefinitionUseCase retireDefinition,
            GetWorkflowDefinitionUseCase getDefinition) {
        this.createDefinition = createDefinition;
        this.publishDefinition = publishDefinition;
        this.retireDefinition = retireDefinition;
        this.getDefinition = getDefinition;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefinitionIdResponse create(@Valid @RequestBody CreateDefinitionRequest request) {
        WorkflowDefinitionId id = createDefinition.execute(new CreateWorkflowDefinitionUseCase.Command(
                request.tenantId(),
                request.key(),
                request.name(),
                request.description(),
                request.steps().stream()
                        .map(s -> new CreateWorkflowDefinitionUseCase.StepCommand(
                                s.stepKey(), s.name(), s.type(), s.assigneeRole()))
                        .toList()));
        return new DefinitionIdResponse(id.value());
    }

    @GetMapping("/{definitionId}")
    public DefinitionResponse get(@PathVariable String definitionId) {
        return DefinitionResponse.from(getDefinition.byId(definitionId));
    }

    @PostMapping("/{definitionId}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publish(@PathVariable String definitionId) {
        publishDefinition.execute(definitionId);
    }

    @PostMapping("/{definitionId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String definitionId) {
        retireDefinition.execute(definitionId);
    }

    public record CreateDefinitionRequest(
            @NotBlank String tenantId,
            @NotBlank String key,
            @NotBlank String name,
            String description,
            @NotEmpty List<StepRequest> steps) {}

    public record StepRequest(
            @NotBlank String stepKey, @NotBlank String name, @NotBlank String type, String assigneeRole) {}

    public record DefinitionIdResponse(String definitionId) {}

    public record DefinitionResponse(
            String definitionId,
            String tenantId,
            String key,
            String name,
            String description,
            int version,
            String status,
            List<StepResponse> steps) {

        static DefinitionResponse from(WorkflowDefinition definition) {
            return new DefinitionResponse(
                    definition.id().value(),
                    definition.tenantId().value(),
                    definition.key().value(),
                    definition.name(),
                    definition.description().orElse(null),
                    definition.version(),
                    definition.status().name(),
                    definition.steps().stream().map(StepResponse::from).toList());
        }
    }

    public record StepResponse(
            String stepKey, String name, String type, String assigneeRole, int ordinal) {

        static StepResponse from(WorkflowStep step) {
            return new StepResponse(
                    step.stepKey(),
                    step.name(),
                    step.type().name(),
                    step.assigneeRole().orElse(null),
                    step.ordinal());
        }
    }
}
