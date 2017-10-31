package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.employee.ChangeEmployeeAssignmentUseCase;
import et.gov.ethgov.directory.application.employee.GetPublicEmployeeUseCase;
import et.gov.ethgov.directory.application.employee.LinkEmployeeIdentityUseCase;
import et.gov.ethgov.directory.application.employee.RegisterPublicEmployeeUseCase;
import et.gov.ethgov.directory.application.employee.SeparatePublicEmployeeUseCase;
import et.gov.ethgov.directory.domain.employee.PublicEmployee;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/public-employees")
public class PublicEmployeeController {

    private final RegisterPublicEmployeeUseCase registerEmployee;
    private final ChangeEmployeeAssignmentUseCase changeAssignment;
    private final LinkEmployeeIdentityUseCase linkIdentity;
    private final SeparatePublicEmployeeUseCase separateEmployee;
    private final GetPublicEmployeeUseCase getEmployee;

    public PublicEmployeeController(
            RegisterPublicEmployeeUseCase registerEmployee,
            ChangeEmployeeAssignmentUseCase changeAssignment,
            LinkEmployeeIdentityUseCase linkIdentity,
            SeparatePublicEmployeeUseCase separateEmployee,
            GetPublicEmployeeUseCase getEmployee) {
        this.registerEmployee = registerEmployee;
        this.changeAssignment = changeAssignment;
        this.linkIdentity = linkIdentity;
        this.separateEmployee = separateEmployee;
        this.getEmployee = getEmployee;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeIdResponse register(@Valid @RequestBody RegisterEmployeeRequest request) {
        PublicEmployeeId id = registerEmployee.execute(new RegisterPublicEmployeeUseCase.Command(
                request.tenantId(),
                request.employeeNumber(),
                request.citizenId(),
                request.displayName(),
                request.organizationalUnitId(),
                request.jobTitle(),
                request.hiredOn()));
        return new EmployeeIdResponse(id.value());
    }

    @GetMapping("/lookup")
    public EmployeeResponse lookup(@RequestParam String tenantId, @RequestParam String employeeNumber) {
        return EmployeeResponse.from(getEmployee.byTenantAndNumber(tenantId, employeeNumber));
    }

    @GetMapping("/{publicEmployeeId}")
    public EmployeeResponse get(@PathVariable String publicEmployeeId) {
        return EmployeeResponse.from(getEmployee.byId(publicEmployeeId));
    }

    @PostMapping("/{publicEmployeeId}/assignment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeAssignment(
            @PathVariable String publicEmployeeId, @Valid @RequestBody AssignmentRequest request) {
        changeAssignment.execute(publicEmployeeId, request.organizationalUnitId(), request.jobTitle());
    }

    @PostMapping("/{publicEmployeeId}/identity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkIdentity(
            @PathVariable String publicEmployeeId, @Valid @RequestBody LinkIdentityRequest request) {
        linkIdentity.execute(publicEmployeeId, request.identityAccountId());
    }

    @PostMapping("/{publicEmployeeId}/separate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void separate(
            @PathVariable String publicEmployeeId, @Valid @RequestBody SeparateRequest request) {
        separateEmployee.execute(publicEmployeeId, request.separatedOn());
    }

    public record RegisterEmployeeRequest(
            @NotBlank String tenantId,
            @NotBlank String employeeNumber,
            @NotBlank String citizenId,
            @NotBlank String displayName,
            @NotBlank String organizationalUnitId,
            @NotBlank String jobTitle,
            @NotBlank String hiredOn) {}

    public record AssignmentRequest(@NotBlank String organizationalUnitId, @NotBlank String jobTitle) {}

    public record LinkIdentityRequest(@NotBlank String identityAccountId) {}

    public record SeparateRequest(@NotBlank String separatedOn) {}

    public record EmployeeIdResponse(String publicEmployeeId) {}

    public record EmployeeResponse(
            String publicEmployeeId,
            String tenantId,
            String employeeNumber,
            String citizenId,
            String displayName,
            String organizationalUnitId,
            String jobTitle,
            String identityAccountId,
            String status,
            String hiredOn,
            String separatedOn) {

        static EmployeeResponse from(PublicEmployee employee) {
            return new EmployeeResponse(
                    employee.id().value(),
                    employee.tenantId().value(),
                    employee.employeeNumber().value(),
                    employee.citizenId(),
                    employee.displayName(),
                    employee.organizationalUnitId().value(),
                    employee.jobTitle(),
                    employee.identityAccountId().orElse(null),
                    employee.status().name(),
                    employee.hiredOn().toString(),
                    employee.separatedOn().map(Object::toString).orElse(null));
        }
    }
}
