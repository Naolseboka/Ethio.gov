package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.definition.CancelReportDefinitionUseCase;
import et.gov.ethgov.reporting.application.definition.GetReportDefinitionUseCase;
import et.gov.ethgov.reporting.application.definition.PublishReportDefinitionUseCase;
import et.gov.ethgov.reporting.application.definition.RegisterReportDefinitionUseCase;
import et.gov.ethgov.reporting.application.definition.RetireReportDefinitionUseCase;
import et.gov.ethgov.reporting.application.definition.UpdateReportDefinitionUseCase;
import et.gov.ethgov.reporting.domain.definition.ReportDefinition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/v1/report-definitions")
public class ReportDefinitionController {

    private final RegisterReportDefinitionUseCase registerDefinition;
    private final UpdateReportDefinitionUseCase updateDefinition;
    private final PublishReportDefinitionUseCase publishDefinition;
    private final RetireReportDefinitionUseCase retireDefinition;
    private final CancelReportDefinitionUseCase cancelDefinition;
    private final GetReportDefinitionUseCase getDefinition;

    public ReportDefinitionController(
            RegisterReportDefinitionUseCase registerDefinition,
            UpdateReportDefinitionUseCase updateDefinition,
            PublishReportDefinitionUseCase publishDefinition,
            RetireReportDefinitionUseCase retireDefinition,
            CancelReportDefinitionUseCase cancelDefinition,
            GetReportDefinitionUseCase getDefinition) {
        this.registerDefinition = registerDefinition;
        this.updateDefinition = updateDefinition;
        this.publishDefinition = publishDefinition;
        this.retireDefinition = retireDefinition;
        this.cancelDefinition = cancelDefinition;
        this.getDefinition = getDefinition;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefinitionIdResponse register(@Valid @RequestBody RegisterRequest request) {
        return new DefinitionIdResponse(registerDefinition
                .execute(new RegisterReportDefinitionUseCase.Command(
                        request.tenantId(),
                        request.code(),
                        request.name(),
                        request.description(),
                        request.category(),
                        request.format(),
                        request.parameterSchema()))
                .value());
    }

    @PutMapping("/{reportDefinitionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable String reportDefinitionId, @Valid @RequestBody UpdateRequest request) {
        updateDefinition.execute(new UpdateReportDefinitionUseCase.Command(
                reportDefinitionId,
                request.name(),
                request.description(),
                request.category(),
                request.format(),
                request.parameterSchema()));
    }

    @GetMapping("/lookup")
    public DefinitionResponse lookup(
            @RequestParam String tenantId,
            @RequestParam(required = false) String reportNumber,
            @RequestParam(required = false) String code) {
        if (code != null && !code.isBlank()) {
            return DefinitionResponse.from(getDefinition.byCode(tenantId, code));
        }
        return DefinitionResponse.from(getDefinition.byNumber(tenantId, reportNumber));
    }

    @GetMapping("/{reportDefinitionId}")
    public DefinitionResponse get(@PathVariable String reportDefinitionId) {
        return DefinitionResponse.from(getDefinition.byId(reportDefinitionId));
    }

    @PostMapping("/{reportDefinitionId}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publish(@PathVariable String reportDefinitionId) {
        publishDefinition.execute(reportDefinitionId);
    }

    @PostMapping("/{reportDefinitionId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String reportDefinitionId) {
        retireDefinition.execute(reportDefinitionId);
    }

    @PostMapping("/{reportDefinitionId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable String reportDefinitionId, @Valid @RequestBody ReasonRequest request) {
        cancelDefinition.execute(reportDefinitionId, request.reason());
    }

    public record RegisterRequest(
            @NotBlank String tenantId,
            @NotBlank String code,
            @NotBlank @Size(max = 200) String name,
            @Size(max = 2000) String description,
            @NotBlank String category,
            @NotBlank String format,
            @NotBlank String parameterSchema) {}

    public record UpdateRequest(
            @NotBlank @Size(max = 200) String name,
            @Size(max = 2000) String description,
            @NotBlank String category,
            @NotBlank String format,
            @NotBlank String parameterSchema) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record DefinitionIdResponse(String reportDefinitionId) {}

    public record DefinitionResponse(
            String reportDefinitionId,
            String tenantId,
            String reportNumber,
            String code,
            String name,
            String description,
            String category,
            String format,
            String parameterSchema,
            String status) {

        static DefinitionResponse from(ReportDefinition definition) {
            return new DefinitionResponse(
                    definition.id().value(),
                    definition.tenantId().value(),
                    definition.reportNumber().value(),
                    definition.code().value(),
                    definition.name(),
                    definition.description().orElse(null),
                    definition.category().name(),
                    definition.format().name(),
                    definition.parameterSchema().json(),
                    definition.status().name());
        }
    }
}
