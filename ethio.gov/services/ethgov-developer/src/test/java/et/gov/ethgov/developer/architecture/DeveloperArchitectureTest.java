package et.gov.ethgov.developer.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import et.gov.ethgov.platform.testing.archunit.EthGovArchitectureRules;

@AnalyzeClasses(
        packages = "et.gov.ethgov.developer",
        importOptions = ImportOption.DoNotIncludeTests.class)
class DeveloperArchitectureTest {

    @ArchTest
    static final ArchRule domainIndependent =
            EthGovArchitectureRules.domainMustNotDependOnFrameworks("et.gov.ethgov.developer.domain");

    @ArchTest
    static final ArchRule applicationIndependentOfAdapters =
            EthGovArchitectureRules.applicationMustNotDependOnAdapters(
                    "et.gov.ethgov.developer.application", "et.gov.ethgov.developer.adapter");

    @ArchTest
    static final ArchRule controllersInWebAdapter =
            EthGovArchitectureRules.controllersMustResideInInboundAdapters("et.gov.ethgov.developer");

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplication = noClasses()
            .that()
            .resideInAPackage("et.gov.ethgov.developer.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("et.gov.ethgov.developer.application..");
}
