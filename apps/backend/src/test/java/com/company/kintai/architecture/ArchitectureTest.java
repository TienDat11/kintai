package com.company.kintai.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.company.kintai")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule domainMustNotDependOnSpring = classes()
        .that().resideInAPackage("..domain..")
        .should().onlyDependOnClassesThat()
        .resideOutsideOfPackages(
            "org.springframework..",
            "jakarta.persistence..",
            "org.mybatis.."
        )
        .because("Domain models must be pure Java — no framework dependencies (spec §4.10 Phase 0–2, ARCH-04)");

    @ArchTest
    static final ArchRule controllerMustNotAccessMapper = classes()
        .that().resideInAPackage("..controller..")
        .should().onlyAccessClassesThat()
        .resideOutsideOfPackages("..mapper..")
        .because("Controllers must not directly access MyBatis mappers (spec §6.1)");

    // ── Spec §4 architecture rules (ARCH-01 through ARCH-16) ────────────
    // Uncomment when the corresponding layer exists in the codebase.
    //
    // @ArchTest
    // static final ArchRule layeredArch = layeredArchitecture()
    //     .consideringAllDependencies()
    //     .layer("Controller").definedBy("..controller..")
    //     .layer("Service").definedBy("..service..")
    //     .layer("Mapper").definedBy("..mapper..")
    //     .layer("Domain").definedBy("..domain..")
    //     .whereLayer("Controller").mayOnlyBeAccessedByLayers("Controller")
    //     .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
    //     .whereLayer("Mapper").mayOnlyBeAccessedByLayers("Service")
    //     .whereLayer("Domain").mayOnlyBeAccessedByLayers("Service", "Mapper", "Domain")
    //     .because("Strict layering per spec §4 (ARCH-01)");
    //
    // @ArchTest
    // static final ArchRule serviceMustNotDependOnController = classes()
    //     .that().resideInAPackage("..service..")
    //     .should().onlyDependOnClassesThat()
    //     .resideOutsideOfPackages("..controller..")
    //     .because("Services must not depend on controllers (ARCH-03)");
    //
    // @ArchTest
    // static final ArchRule domainShouldNotDependOnService = classes()
    //     .that().resideInAPackage("..domain..")
    //     .should().onlyDependOnClassesThat()
    //     .resideOutsideOfPackages("..service..")
    //     .because("Domain should not depend on services (ARCH-05)");
    //
    // @ArchTest
    // static final ArchRule naming_ServiceClasses = classes()
    //     .that().resideInAPackage("..service..")
    //     .and().areNotInterfaces()
    //     .should().haveSimpleNameEndingWith("ServiceImpl")
    //     .orShould().haveSimpleNameEndingWith("Service")
    //     .because("Service implementations should follow naming convention (ARCH-06)");
    //
    // @ArchTest
    // static final ArchRule naming_MapperInterfaces = classes()
    //     .that().resideInAPackage("..mapper..")
    //     .should().beInterfaces()
    //     .andShould().haveSimpleNameEndingWith("Mapper")
    //     .because("MyBatis mappers should be interfaces named *Mapper (ARCH-08)");
    //
    // @ArchTest
    // static final ArchRule naming_DomainModels = classes()
    //     .that().resideInAPackage("..domain..")
    //     .should().haveSimpleNameNotEndingWith("Mapper")
    //     .andShould().haveSimpleNameNotEndingWith("Controller")
    //     .andShould().haveSimpleNameNotEndingWith("Service")
    //     .because("Domain classes must not use infrastructure naming patterns (ARCH-09)");
    //
    // @ArchTest
    // static final ArchRule controllerShouldNotReturnEntity = classes()
    //     .that().resideInAPackage("..controller..")
    //     .should().notAccessClassesThat()
    //     .resideInAPackage("..domain..")
    //     .because("Controllers should return DTOs, not domain entities (ARCH-10)");
    //
    // @ArchTest
    // static final ArchRule domainClassesShouldBePackagePrivate = classes()
    //     .that().resideInAPackage("..domain..")
    //     .and().areNotInterfaces()
    //     .should().bePackagePrivate()
    //     .because("Domain implementation details should be package-private (ARCH-12)");
    //
    // @ArchTest
    // static final ArchRule noCyclicDependencies = classes()
    //     .should().notDependOnEachOther()
    //     .because("Package cycles are prohibited (ARCH-14)");
    //
    // @ArchTest
    // static final ArchRule testClassesShouldResideInTestPackage = classes()
    //     .that().haveSimpleNameEndingWith("Test")
    //     .should().resideInAnyPackage("..architecture..", "..test..", "..it..")
    //     .because("Test classes should live in designated test packages (ARCH-16)");
}
