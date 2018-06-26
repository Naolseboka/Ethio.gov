package et.gov.ethgov.reporting.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import et.gov.ethgov.platform.testing.archunit.EthGovArchitectureRules;

@AnalyzeClasses(
        packages = "et.gov.ethgov.reporting",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ReportingArchitectureTest {

    @ArchTest
    static final ArchRule domainIndependent =
            EthGovArchitectureRules.domainMustNotDependOnFrameworks("et.gov.ethgov.reporting.domain");

    @ArchTest
    static final ArchRule applicationIndependentOfAdapters =
            EthGovArchitectureRules.applicationMustNotDependOnAdapters(
                    "et.gov.ethgov.reporting.application", "et.gov.ethgov.reporting.adapter");

    @ArchTest
    static final ArchRule controllersInWebAdapter =
            EthGovArchitectureRules.controllersMustResideInInboundAdapters("et.gov.ethgov.reporting");

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplication = noClasses()
            .that()
            .resideInAPackage("et.gov.ethgov.reporting.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("et.gov.ethgov.reporting.application..");
}
