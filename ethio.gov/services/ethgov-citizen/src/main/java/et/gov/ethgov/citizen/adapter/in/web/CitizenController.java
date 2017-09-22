package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.citizen.AssignFaydaIdUseCase;
import et.gov.ethgov.citizen.application.citizen.GetCitizenUseCase;
import et.gov.ethgov.citizen.application.citizen.LinkIdentityAccountUseCase;
import et.gov.ethgov.citizen.application.citizen.RecordCitizenDeathUseCase;
import et.gov.ethgov.citizen.application.citizen.RegisterCitizenUseCase;
import et.gov.ethgov.citizen.application.citizen.UpdateCitizenDemographicsUseCase;
import et.gov.ethgov.citizen.domain.citizen.Citizen;
import et.gov.ethgov.citizen.domain.citizen.CitizenId;
import et.gov.ethgov.platform.commons.error.DomainException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Optional;
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
@RequestMapping("/v1/citizens")
public class CitizenController {

    private final RegisterCitizenUseCase registerCitizen;
    private final UpdateCitizenDemographicsUseCase updateDemographics;
    private final AssignFaydaIdUseCase assignFaydaId;
    private final LinkIdentityAccountUseCase linkIdentityAccount;
    private final RecordCitizenDeathUseCase recordDeath;
    private final GetCitizenUseCase getCitizen;

    public CitizenController(
            RegisterCitizenUseCase registerCitizen,
            UpdateCitizenDemographicsUseCase updateDemographics,
            AssignFaydaIdUseCase assignFaydaId,
            LinkIdentityAccountUseCase linkIdentityAccount,
            RecordCitizenDeathUseCase recordDeath,
            GetCitizenUseCase getCitizen) {
        this.registerCitizen = registerCitizen;
        this.updateDemographics = updateDemographics;
        this.assignFaydaId = assignFaydaId;
        this.linkIdentityAccount = linkIdentityAccount;
        this.recordDeath = recordDeath;
        this.getCitizen = getCitizen;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitizenIdResponse register(@Valid @RequestBody RegisterCitizenRequest request) {
        CitizenId id = registerCitizen.execute(toRegisterCommand(request));
        return new CitizenIdResponse(id.value());
    }

    @GetMapping("/{citizenId}")
    public CitizenResponse get(@PathVariable String citizenId) {
        return CitizenResponse.from(getCitizen.byId(citizenId));
    }

    @GetMapping("/search")
    public CitizenResponse search(
            @RequestParam(required = false) String phone, @RequestParam(required = false) String faydaId) {
        Optional<Citizen> found;
        if (phone != null && !phone.isBlank()) {
            found = getCitizen.byPhone(phone);
        } else if (faydaId != null && !faydaId.isBlank()) {
            found = getCitizen.byFaydaId(faydaId);
        } else {
            throw new IllegalArgumentException("phone or faydaId query parameter is required");
        }
        return found.map(CitizenResponse::from)
                .orElseThrow(() -> new DomainException(
                        "CITIZEN_NOT_FOUND", "Citizen Not Found", "Citizen does not exist", 404));
    }

    @PutMapping("/{citizenId}/demographics")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDemographics(
            @PathVariable String citizenId, @Valid @RequestBody UpdateDemographicsRequest request) {
        updateDemographics.execute(toUpdateCommand(citizenId, request));
    }

    @PostMapping("/{citizenId}/fayda")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignFayda(@PathVariable String citizenId, @Valid @RequestBody AssignFaydaRequest request) {
        assignFaydaId.execute(citizenId, request.faydaId());
    }

    @PostMapping("/{citizenId}/identity-link")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkIdentity(
            @PathVariable String citizenId, @Valid @RequestBody LinkIdentityRequest request) {
        linkIdentityAccount.execute(citizenId, request.identityAccountId());
    }

    @PostMapping("/{citizenId}/death")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recordDeath(@PathVariable String citizenId, @Valid @RequestBody DeathRequest request) {
        recordDeath.execute(citizenId, request.deceasedOn());
    }

    private static RegisterCitizenUseCase.Command toRegisterCommand(RegisterCitizenRequest request) {
        return new RegisterCitizenUseCase.Command(
                request.givenName(),
                request.fatherName(),
                request.grandfatherName(),
                request.dateOfBirth(),
                request.sex(),
                request.phone(),
                request.faydaId(),
                request.registeringTenantId(),
                request.regionCode(),
                request.zoneCode(),
                request.zoneName(),
                request.woredaCode(),
                request.woredaName(),
                request.kebeleCode(),
                request.kebeleName(),
                request.locality(),
                request.street(),
                request.houseNumber());
    }

    private static UpdateCitizenDemographicsUseCase.Command toUpdateCommand(
            String citizenId, UpdateDemographicsRequest request) {
        return new UpdateCitizenDemographicsUseCase.Command(
                citizenId,
                request.givenName(),
                request.fatherName(),
                request.grandfatherName(),
                request.dateOfBirth(),
                request.sex(),
                request.phone(),
                request.regionCode(),
                request.zoneCode(),
                request.zoneName(),
                request.woredaCode(),
                request.woredaName(),
                request.kebeleCode(),
                request.kebeleName(),
                request.locality(),
                request.street(),
                request.houseNumber());
    }

    public record RegisterCitizenRequest(
            @NotBlank String givenName,
            @NotBlank String fatherName,
            String grandfatherName,
            @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String dateOfBirth,
            @NotBlank String sex,
            @NotBlank String phone,
            String faydaId,
            @NotBlank String registeringTenantId,
            @NotBlank String regionCode,
            @NotBlank String zoneCode,
            @NotBlank String zoneName,
            @NotBlank String woredaCode,
            @NotBlank String woredaName,
            @NotBlank String kebeleCode,
            @NotBlank String kebeleName,
            String locality,
            String street,
            String houseNumber) {}

    public record UpdateDemographicsRequest(
            @NotBlank String givenName,
            @NotBlank String fatherName,
            String grandfatherName,
            @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String dateOfBirth,
            @NotBlank String sex,
            @NotBlank String phone,
            @NotBlank String regionCode,
            @NotBlank String zoneCode,
            @NotBlank String zoneName,
            @NotBlank String woredaCode,
            @NotBlank String woredaName,
            @NotBlank String kebeleCode,
            @NotBlank String kebeleName,
            String locality,
            String street,
            String houseNumber) {}

    public record AssignFaydaRequest(@NotBlank String faydaId) {}

    public record LinkIdentityRequest(@NotBlank String identityAccountId) {}

    public record DeathRequest(@NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String deceasedOn) {}

    public record CitizenIdResponse(String citizenId) {}

    public record CitizenResponse(
            String citizenId,
            String givenName,
            String fatherName,
            String grandfatherName,
            String fullName,
            String dateOfBirth,
            String dateOfBirthEthiopian,
            String sex,
            String phone,
            String faydaId,
            String identityAccountId,
            String registeringTenantId,
            String status,
            String deceasedOn,
            String regionCode,
            String zoneCode,
            String woredaCode,
            String kebeleCode) {

        static CitizenResponse from(Citizen citizen) {
            return new CitizenResponse(
                    citizen.id().value(),
                    citizen.name().givenName(),
                    citizen.name().fatherName(),
                    citizen.name().grandfatherName(),
                    citizen.name().fullName(),
                    citizen.dateOfBirth().toString(),
                    citizen.dateOfBirthEthiopian().toString(),
                    citizen.sex().name(),
                    citizen.phone().e164(),
                    citizen.faydaId().map(Object::toString).orElse(null),
                    citizen.identityAccountId().orElse(null),
                    citizen.registeringTenantId().value(),
                    citizen.status().name(),
                    citizen.deceasedOn().map(Object::toString).orElse(null),
                    citizen.residenceAddress().regionalState().code(),
                    citizen.residenceAddress().zoneCode(),
                    citizen.residenceAddress().woredaCode(),
                    citizen.residenceAddress().kebeleCode());
        }
    }
}
