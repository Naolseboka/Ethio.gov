package et.gov.ethgov.citizen.domain.citizen;

import et.gov.ethgov.platform.commons.address.EthiopianAddress;
import et.gov.ethgov.platform.commons.calendar.EthiopianDate;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Citizen registry aggregate — authoritative demographic record for a natural person.
 */
public final class Citizen {

    private final CitizenId id;
    private PersonName name;
    private LocalDate dateOfBirth;
    private Sex sex;
    private EthiopianAddress residenceAddress;
    private EthiopianPhoneNumber phone;
    private FaydaId faydaId;
    private String identityAccountId;
    private final TenantId registeringTenantId;
    private CitizenStatus status;
    private LocalDate deceasedOn;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<CitizenDomainEvent> domainEvents = new ArrayList<>();

    private Citizen(
            CitizenId id,
            PersonName name,
            LocalDate dateOfBirth,
            Sex sex,
            EthiopianAddress residenceAddress,
            EthiopianPhoneNumber phone,
            FaydaId faydaId,
            String identityAccountId,
            TenantId registeringTenantId,
            CitizenStatus status,
            LocalDate deceasedOn,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.residenceAddress = residenceAddress;
        this.phone = phone;
        this.faydaId = faydaId;
        this.identityAccountId = identityAccountId;
        this.registeringTenantId = registeringTenantId;
        this.status = status;
        this.deceasedOn = deceasedOn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Citizen register(
            PersonName name,
            LocalDate dateOfBirth,
            Sex sex,
            EthiopianAddress residenceAddress,
            EthiopianPhoneNumber phone,
            FaydaId faydaId,
            TenantId registeringTenantId,
            Instant now,
            LocalDate today) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sex, "sex");
        Objects.requireNonNull(residenceAddress, "residenceAddress");
        Objects.requireNonNull(phone, "phone");
        Objects.requireNonNull(registeringTenantId, "registeringTenantId");
        validateDateOfBirth(dateOfBirth, today);
        Citizen citizen = new Citizen(
                CitizenId.generate(),
                name,
                dateOfBirth,
                sex,
                residenceAddress,
                phone,
                faydaId,
                null,
                registeringTenantId,
                CitizenStatus.ACTIVE,
                null,
                now,
                now,
                0);
        citizen.record(new CitizenRegistered(citizen.id, registeringTenantId, phone, now));
        return citizen;
    }

    public static Citizen rehydrate(
            CitizenId id,
            PersonName name,
            LocalDate dateOfBirth,
            Sex sex,
            EthiopianAddress residenceAddress,
            EthiopianPhoneNumber phone,
            FaydaId faydaId,
            String identityAccountId,
            TenantId registeringTenantId,
            CitizenStatus status,
            LocalDate deceasedOn,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Citizen(
                id,
                name,
                dateOfBirth,
                sex,
                residenceAddress,
                phone,
                faydaId,
                identityAccountId,
                registeringTenantId,
                status,
                deceasedOn,
                createdAt,
                updatedAt,
                version);
    }

    public void updateDemographics(
            PersonName name,
            LocalDate dateOfBirth,
            Sex sex,
            EthiopianAddress residenceAddress,
            EthiopianPhoneNumber phone,
            Instant now,
            LocalDate today) {
        ensureMutable();
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sex, "sex");
        Objects.requireNonNull(residenceAddress, "residenceAddress");
        Objects.requireNonNull(phone, "phone");
        validateDateOfBirth(dateOfBirth, today);
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.residenceAddress = residenceAddress;
        this.phone = phone;
        touch(now);
        record(new CitizenDemographicsUpdated(id, now));
    }

    public void assignFaydaId(FaydaId faydaId, Instant now) {
        ensureMutable();
        Objects.requireNonNull(faydaId, "faydaId");
        if (this.faydaId != null && !this.faydaId.equals(faydaId)) {
            throw new DomainException(
                    "FAYDA_ALREADY_ASSIGNED",
                    "Fayda Already Assigned",
                    "Citizen already has a Fayda ID; corrections require a controlled amendment process",
                    409);
        }
        this.faydaId = faydaId;
        touch(now);
        record(new CitizenFaydaAssigned(id, faydaId, now));
    }

    public void linkIdentityAccount(String identityAccountId, Instant now) {
        ensureMutable();
        Objects.requireNonNull(identityAccountId, "identityAccountId");
        String normalized = identityAccountId.trim();
        if (!normalized.startsWith("usr_")) {
            throw new IllegalArgumentException("identityAccountId must be a UserAccountId");
        }
        if (this.identityAccountId != null && !this.identityAccountId.equals(normalized)) {
            throw new DomainException(
                    "IDENTITY_ALREADY_LINKED",
                    "Identity Already Linked",
                    "Citizen is already linked to an identity account",
                    409);
        }
        this.identityAccountId = normalized;
        touch(now);
        record(new CitizenIdentityLinked(id, normalized, now));
    }

    public void recordDeath(LocalDate deceasedOn, Instant now, LocalDate today) {
        Objects.requireNonNull(deceasedOn, "deceasedOn");
        if (status == CitizenStatus.DECEASED) {
            return;
        }
        if (status == CitizenStatus.ARCHIVED) {
            throw new DomainException(
                    "CITIZEN_ARCHIVED", "Citizen Archived", "Archived citizens cannot be updated", 409);
        }
        if (deceasedOn.isAfter(today)) {
            throw new IllegalArgumentException("deceasedOn cannot be in the future");
        }
        if (deceasedOn.isBefore(dateOfBirth)) {
            throw new IllegalArgumentException("deceasedOn cannot precede date of birth");
        }
        this.status = CitizenStatus.DECEASED;
        this.deceasedOn = deceasedOn;
        touch(now);
        record(new CitizenDeceased(id, deceasedOn, now));
    }

    public EthiopianDate dateOfBirthEthiopian() {
        return EthiopianDate.fromGregorian(dateOfBirth);
    }

    public int ageInYears(LocalDate onDate) {
        return Period.between(dateOfBirth, onDate).getYears();
    }

    public List<CitizenDomainEvent> pullDomainEvents() {
        List<CitizenDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void ensureMutable() {
        if (status == CitizenStatus.DECEASED) {
            throw new DomainException(
                    "CITIZEN_DECEASED", "Citizen Deceased", "Deceased citizens cannot be modified", 409);
        }
        if (status == CitizenStatus.ARCHIVED) {
            throw new DomainException(
                    "CITIZEN_ARCHIVED", "Citizen Archived", "Archived citizens cannot be modified", 409);
        }
    }

    private static void validateDateOfBirth(LocalDate dateOfBirth, LocalDate today) {
        Objects.requireNonNull(dateOfBirth, "dateOfBirth");
        Objects.requireNonNull(today, "today");
        if (dateOfBirth.isAfter(today)) {
            throw new IllegalArgumentException("dateOfBirth cannot be in the future");
        }
        int age = Period.between(dateOfBirth, today).getYears();
        if (age > 150) {
            throw new IllegalArgumentException("dateOfBirth implies age over 150 years");
        }
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(CitizenDomainEvent event) {
        domainEvents.add(event);
    }

    public CitizenId id() {
        return id;
    }

    public PersonName name() {
        return name;
    }

    public LocalDate dateOfBirth() {
        return dateOfBirth;
    }

    public Sex sex() {
        return sex;
    }

    public EthiopianAddress residenceAddress() {
        return residenceAddress;
    }

    public EthiopianPhoneNumber phone() {
        return phone;
    }

    public Optional<FaydaId> faydaId() {
        return Optional.ofNullable(faydaId);
    }

    public Optional<String> identityAccountId() {
        return Optional.ofNullable(identityAccountId);
    }

    public TenantId registeringTenantId() {
        return registeringTenantId;
    }

    public CitizenStatus status() {
        return status;
    }

    public Optional<LocalDate> deceasedOn() {
        return Optional.ofNullable(deceasedOn);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }
}
