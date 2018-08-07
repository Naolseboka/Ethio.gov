package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.instance.CancelProcessInstanceUseCase;
import et.gov.ethgov.workflow.application.instance.GetProcessInstanceUseCase;
import et.gov.ethgov.workflow.application.instance.StartProcessInstanceUseCase;
import et.gov.ethgov.workflow.domain.instance.ProcessInstance;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/process-instances")
public class ProcessInstanceController {

    private final StartProcessInstanceUseCase startProcess;
    private final CancelProcessInstanceUseCase cancelProcess;
    private final GetProcessInstanceUseCase getProcess;

    public ProcessInstanceController(
            StartProcessInstanceUseCase startProcess,
            CancelProcessInstanceUseCase cancelProcess,
            GetProcessInstanceUseCase getProcess) {
        this.startProcess = startProcess;
        this.cancelProcess = cancelProcess;
        this.getProcess = getProcess;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessIdResponse start(@Valid @RequestBody StartProcessRequest request) {
        ProcessInstanceId id = startProcess.execute(new StartProcessInstanceUseCase.Command(
                request.tenantId(),
                request.definitionKey(),
                request.businessKey(),
                request.startedByAccountId()));
        return new ProcessIdResponse(id.value());
    }

    @GetMapping("/{processInstanceId}")
    public ProcessResponse get(@PathVariable String processInstanceId) {
        return ProcessResponse.from(getProcess.byId(processInstanceId));
    }

    @PostMapping("/{processInstanceId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable String processInstanceId, @Valid @RequestBody CancelProcessRequest request) {
        cancelProcess.execute(processInstanceId, request.reason());
    }

    public record StartProcessRequest(
            @NotBlank String tenantId,
            @NotBlank String definitionKey,
            @NotBlank String businessKey,
            @NotBlank String startedByAccountId) {}

    public record CancelProcessRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record ProcessIdResponse(String processInstanceId) {}

    public record ProcessResponse(
            String processInstanceId,
            String tenantId,
            String definitionId,
            String businessKey,
            String status,
            int currentStepOrdinal,
            String currentStepKey,
            String startedByAccountId) {

        static ProcessResponse from(ProcessInstance instance) {
            return new ProcessResponse(
                    instance.id().value(),
                    instance.tenantId().value(),
                    instance.definitionId().value(),
                    instance.businessKey(),
                    instance.status().name(),
                    instance.currentStepOrdinal(),
                    instance.currentStepKey(),
                    instance.startedByAccountId());
        }
    }
}
