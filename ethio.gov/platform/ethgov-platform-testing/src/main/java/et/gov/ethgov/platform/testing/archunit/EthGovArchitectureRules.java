package et.gov.ethgov.platform.testing.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.lang.ArchRule;

/**
 * Shared architecture rules enforced by every deployable EthGov service.
 */
public final class EthGovArchitectureRules {

    private EthGovArchitectureRules() {}

    public static ArchRule domainMustNotDependOnFrameworks(String domainPackage) {
        return noClasses()
                .that()
                .resideInAPackage(domainPackage + "..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "org.apache.kafka..")
                .because("Domain model must remain framework-agnostic (hexagonal / DDD)");
    }

    public static ArchRule applicationMustNotDependOnAdapters(String applicationPackage, String adapterPackage) {
        return noClasses()
                .that()
                .resideInAPackage(applicationPackage + "..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage(adapterPackage + "..")
                .because("Application layer must not depend on adapters");
    }

    public static ArchRule controllersMustResideInInboundAdapters(String basePackage) {
        return classes()
                .that()
                .haveSimpleNameEndingWith("Controller")
                .should()
                .resideInAPackage(basePackage + ".adapter.in.web..")
                .because("HTTP controllers belong in inbound web adapters");
    }
}
