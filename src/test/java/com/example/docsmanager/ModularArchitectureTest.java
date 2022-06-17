package com.example.docsmanager;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SliceRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "com.example.docsmanager")
public class ModularArchitectureTest {

  @ArchTest
  final ArchRule core_domain_classes_should_not_have_any_external_dependencies =
      ArchRuleDefinition.noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..boot..", "..adapter..", "..org.springframework..");

  @ArchTest
  final ArchRule public_rest_controller_methods_should_be_annotated_with_respective_HTTP_mapping =
      ArchRuleDefinition.methods()
          .that()
          .arePublic()
          .and()
          .areDeclaredInClassesThat()
          .resideInAPackage("..adapters.in..")
          .and()
          .areDeclaredInClassesThat()
          .haveSimpleNameEndingWith("Controller")
          .and()
          .areDeclaredInClassesThat()
          .areAnnotatedWith(Controller.class)
          .or()
          .areDeclaredInClassesThat()
          .areAnnotatedWith(RestController.class)
          .should()
          .beAnnotatedWith(RequestMapping.class)
          .orShould()
          .beAnnotatedWith(GetMapping.class)
          .orShould()
          .beAnnotatedWith(PostMapping.class)
          .orShould()
          .beAnnotatedWith(PatchMapping.class)
          .orShould()
          .beAnnotatedWith(PutMapping.class)
          .orShould()
          .beAnnotatedWith(DeleteMapping.class);

  @ArchTest
  final SliceRule no_cyclic_dependencies_allowed =
      SlicesRuleDefinition.slices()
          .matching("com.example.docsmanager.domain.(*)..")
          .should()
          .beFreeOfCycles();

  @ArchTest
  final SliceRule adapters_should_not_depend_on_each_other =
      SlicesRuleDefinition.slices()
          .matching("com.example.docsmanager.adapters.(**)..")
          .should()
          .notDependOnEachOther();
}
