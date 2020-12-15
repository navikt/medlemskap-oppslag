
extra["cxfVersion"] = "3.3.8"
extra["cxfPluginVersion"] = "3.2.12"

val ktorVersion = "1.3.2"
val jacksonVersion = "2.10.5"
val prometheusVersion = "0.9.0"
val logbackVersion = "1.2.3"
val logstashVersion = "6.4"
val konfigVersion = "1.6.10.0"
val kotlinLoggerVersion = "1.8.3"
val tjenestespesifikasjonerVersion = "1.2019.12.18-12.22-ce897c4eb2c1"
val cxfVersion = "3.3.8"
val coroutinesVersion = "1.3.7"
val wireMockVersion = "2.27.2"
val mockkVersion = "1.10.0"
val junitJupiterVersion = "5.6.2"
val assertkVersion = "0.22"
val restAssuredVersion = "4.3.2"
val resilience4jVersion = "1.5.0"
val threetenVersion = "1.5.0"
val cucumberVersion = "6.8.1"
val nocommonsVersion = "0.9.0"
val graphqlKotlinClientVersion = "3.6.1"
val archUnitVersion = "0.14.1"
val jsonassertVersion = "1.5.0"
val xmlSchemaVersion = "2.2.5"
val jaxwsToolsVersion = "2.3.1"
val activationVersion = "1.1.1"
val nvi18nVersion = "1.27"
val kotestVersion = "4.2.5"
val swaggerRequestValidatorVersion = "2.11.1"
val swaggerUiVersion = "3.37.2"
// Temporary to fix high severity Snyk vulnerabilities:
val nettyCodecVersion = "4.1.54.Final"
val commonsCodecVersion = "3.2.2"
val httpClientVersion = "4.5.13"
val jettyWebAppVersion = "9.4.35.v20201120"
val jacksonDataformatYamlVersion = "2.10.4"
val guavaVersion = "30.0-jre"
val junitVersion = "4.13.1"

val mainClass = "no.nav.medlemskap.ApplicationKt"

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://kotlin.bintray.com/kotlinx")
}

plugins {
    id("java")
    id("no.nils.wsdl2java") version "0.12"
}

dependencies {
    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("javax.activation:activation:$activationVersion")
    implementation("org.apache.ws.xmlschema:xmlschema-core:$xmlSchemaVersion")
    implementation("com.sun.xml.ws:jaxws-tools:$jaxwsToolsVersion") {
        exclude(group = "com.sun.xml.ws", module = "policy")
    }

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








