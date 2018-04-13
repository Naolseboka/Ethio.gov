package et.gov.ethgov.opendata.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import et.gov.ethgov.platform.testing.archunit.EthGovArchitectureRules;

@AnalyzeClasses(
        packages = "et.gov.ethgov.opendata",
        importOptions = ImportOption.DoNotIncludeTests.class)
class OpenDataArchitectureTest {

    @ArchTest
    static final ArchRule domainIndependent =
            EthGovArchitectureRules.domainMustNotDependOnFrameworks("et.gov.ethgov.opendata.domain");

    @ArchTest
    static final ArchRule applicationIndependentOfAdapters =
            EthGovArchitectureRules.applicationMustNotDependOnAdapters(
                    "et.gov.ethgov.opendata.application", "et.gov.ethgov.opendata.adapter");

    @ArchTest
    static final ArchRule controllersInWebAdapter =
            EthGovArchitectureRules.controllersMustResideInInboundAdapters("et.gov.ethgov.opendata");

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplication = noClasses()
            .that()
            .resideInAPackage("et.gov.ethgov.opendata.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("et.gov.ethgov.opendata.application..");
}
