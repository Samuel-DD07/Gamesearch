package fr.epita.apping.fullstack.gamesearch.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Clean Architecture Validation (ArchUnit)")
class CleanArchitectureTest {

  private static final String BASE_PACKAGE = "fr.epita.apping.fullstack.gamesearch";

  private static JavaClasses classes;

  @BeforeAll
  static void importClasses() {
    classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);
  }

  @Nested
  @DisplayName("Layer dependency rules")
  class LayerDependencies {

    @Test
    @DisplayName("presentation layer must not be accessed by domain or data layers")
    void presentationIsNotAccessedByLowerLayers() {
      ArchRule rule =
          layeredArchitecture()
              .consideringOnlyDependenciesInLayers()
              .layer("Presentation")
              .definedBy(BASE_PACKAGE + ".presentation.rest..")
              .layer("Domain")
              .definedBy(BASE_PACKAGE + ".domain..")
              .layer("Data")
              .definedBy(BASE_PACKAGE + ".data..")
              .whereLayer("Presentation")
              .mayNotBeAccessedByAnyLayer()
              .whereLayer("Domain")
              .mayOnlyBeAccessedByLayers("Presentation")
              .whereLayer("Data")
              .mayOnlyBeAccessedByLayers("Presentation", "Domain");

      rule.check(classes);
    }
  }

  @Nested
  @DisplayName("Package naming conventions")
  class PackageNaming {

    @Test
    @DisplayName("all REST controllers reside in the 'presentation.rest' package")
    void controllersResideInPresentationRestPackage() {
      ArchRule rule =
          classes()
              .that()
              .areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
              .should()
              .resideInAPackage(BASE_PACKAGE + ".presentation.rest");

      rule.check(classes);
    }

    @Test
    @DisplayName("all @Service beans reside in the 'domain.service' package")
    void servicesResideInDomainServicePackage() {
      ArchRule rule =
          classes()
              .that()
              .areAnnotatedWith(org.springframework.stereotype.Service.class)
              .and()
              .haveNameNotMatching(".*Producer|.*Consumer|.*ApiKeyService|.*JwtService")
              .should()
              .resideInAPackage(BASE_PACKAGE + ".domain.service");

      rule.check(classes);
    }

    @Test
    @DisplayName("all JPA entities reside in the 'data.model' package")
    void jpaEntitiesResideInDataModelPackage() {
      ArchRule rule =
          classes()
              .that()
              .areAnnotatedWith(jakarta.persistence.Entity.class)
              .should()
              .resideInAPackage(BASE_PACKAGE + ".data.model");

      rule.check(classes);
    }

    @Test
    @DisplayName("all JPA repositories reside in the 'data.repository' package")
    void repositoriesResideInDataRepositoryPackage() {
      ArchRule rule =
          classes()
              .that()
              .areInterfaces()
              .and()
              .haveSimpleNameEndingWith("Repository")
              .should()
              .resideInAPackage(BASE_PACKAGE + ".data.repository");

      rule.check(classes);
    }
  }

  @Nested
  @DisplayName("Domain entity independence")
  class DomainEntityIndependence {

    @Test
    @DisplayName("domain entities must not depend on JPA annotations")
    void domainEntitiesDoNotDependOnJpa() {
      ArchRule rule =
          noClasses()
              .that()
              .resideInAPackage(BASE_PACKAGE + ".domain.entity..")
              .should()
              .dependOnClassesThat()
              .resideInAPackage("jakarta.persistence..");

      rule.check(classes);
    }

    @Test
    @DisplayName("domain entities must not depend on Spring framework annotations")
    void domainEntitiesDoNotDependOnSpring() {
      ArchRule rule =
          noClasses()
              .that()
              .resideInAPackage(BASE_PACKAGE + ".domain.entity..")
              .should()
              .dependOnClassesThat()
              .resideInAPackage("org.springframework..");

      rule.check(classes);
    }
  }

  @Nested
  @DisplayName("Data model isolation")
  class DataModelIsolation {

    @Test
    @DisplayName("data models must not depend on presentation layer classes")
    void dataModelsDoNotDependOnPresentation() {
      ArchRule rule =
          noClasses()
              .that()
              .resideInAPackage(BASE_PACKAGE + ".data..")
              .should()
              .dependOnClassesThat()
              .resideInAPackage(BASE_PACKAGE + ".presentation..");

      rule.check(classes);
    }
  }

  @Nested
  @DisplayName("Exception handling conventions")
  class ExceptionHandling {

    @Test
    @DisplayName("all custom exceptions reside in the 'exception' package")
    void customExceptionsResideInExceptionPackage() {
      ArchRule rule =
          classes()
              .that()
              .areAssignableTo(RuntimeException.class)
              .and()
              .resideInAPackage(BASE_PACKAGE + "..")
              .should()
              .resideInAPackage(BASE_PACKAGE + ".exception");

      rule.check(classes);
    }

    @Test
    @DisplayName("the global exception handler uses @RestControllerAdvice annotation")
    void globalHandlerIsAnnotatedWithRestControllerAdvice() {
      ArchRule rule =
          classes()
              .that()
              .haveSimpleName("GlobalHandlerException")
              .should()
              .beAnnotatedWith(org.springframework.web.bind.annotation.RestControllerAdvice.class);

      rule.check(classes);
    }
  }
}
