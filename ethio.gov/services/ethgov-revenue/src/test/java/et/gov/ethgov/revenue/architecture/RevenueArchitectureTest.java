package et.gov.ethgov.revenue.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import et.gov.ethgov.platform.testing.archunit.EthGovArchitectureRules;

@AnalyzeClasses(
        packages = "et.gov.ethgov.revenue",
        importOptions = ImportOption.DoNotIncludeTests.class)
class RevenueArchitectureTest {

    @ArchTest
    static final ArchRule domainIndependent =
            EthGovArchitectureRules.domainMustNotDependOnFrameworks("et.gov.ethgov.revenue.domain");

    @ArchTest
    static final ArchRule applicationIndependentOfAdapters =
            EthGovArchitectureRules.applicationMustNotDependOnAdapters(
                    "et.gov.ethgov.revenue.application", "et.gov.ethgov.revenue.adapter");

    @ArchTest
    static final ArchRule controllersInWebAdapter =
            EthGovArchitectureRules.controllersMustResideInInboundAdapters("et.gov.ethgov.revenue");

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplication = noClasses()
            .that()
            .resideInAPackage("et.gov.ethgov.revenue.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("et.gov.ethgov.revenue.application..");
}
