
extra["cxfVersion"] = "3.3.8"
extra["cxfPluginVersion"] = "3.2.12"

repositories {
    jcenter()
    mavenCentral()
}

plugins {
    id("java")
    id("no.nils.wsdl2java") version "0.12"
}

dependencies {

}

wsdl2java {
    wsdlDir = file("$projectDir/src/main/wsdl")
    wsdlsToGenerate = listOf(
        listOf("$wsdlDir/MT_1067_NAV_v1.wsdl")
    )
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}








