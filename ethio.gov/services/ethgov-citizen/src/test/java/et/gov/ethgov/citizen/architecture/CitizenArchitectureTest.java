package et.gov.ethgov.citizen.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import et.gov.ethgov.platform.testing.archunit.EthGovArchitectureRules;

@AnalyzeClasses(
        packages = "et.gov.ethgov.citizen",
        importOptions = ImportOption.DoNotIncludeTests.class)
class CitizenArchitectureTest {

    @ArchTest
    static final ArchRule domainIndependent =
            EthGovArchitectureRules.domainMustNotDependOnFrameworks("et.gov.ethgov.citizen.domain");

    @ArchTest
    static final ArchRule applicationIndependentOfAdapters =
            EthGovArchitectureRules.applicationMustNotDependOnAdapters(
                    "et.gov.ethgov.citizen.application", "et.gov.ethgov.citizen.adapter");

    @ArchTest
    static final ArchRule controllersInWebAdapter =
            EthGovArchitectureRules.controllersMustResideInInboundAdapters("et.gov.ethgov.citizen");

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplication = noClasses()
            .that()
            .resideInAPackage("et.gov.ethgov.citizen.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("et.gov.ethgov.citizen.application..");
}
