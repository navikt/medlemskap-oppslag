package no.nav.medlemskap.domene

import com.tngtech.archunit.junit.AnalyzeClasses

@AnalyzeClasses(packages = ["no.nav.medlemskap"])
class DomeneDependenciesTest {

    //@ArchTest
    //val `klasser i domenet skal ikke importere eksterne klasser fra baksystemene` =
    //noClasses().that().resideInAPackage("no.nav.medlemskap.domene")
    //        .should().dependOnClassesThat().resideInAPackage("no.nav.medlemskap.clients..")
}