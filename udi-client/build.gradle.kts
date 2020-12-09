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

val githubUser: String by project
val githubPassword: String by project

extra["cxfVersion"] = "3.3.2"
extra["cxfPluginVersion"] = "3.2.2"

fun tjenestespesifikasjon(name: String) = "no.nav.tjenestespesifikasjoner:$name:$tjenestespesifikasjonerVersion"

plugins {
    id("java")
    id("no.nils.wsdl2java") version "0.12"
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("org.threeten:threeten-extra:$threetenVersion")

    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("javax.activation:activation:$activationVersion")
    implementation("org.apache.ws.xmlschema:xmlschema-core:$xmlSchemaVersion")
    implementation("com.sun.xml.ws:jaxws-tools:$jaxwsToolsVersion") {
        exclude(group = "com.sun.xml.ws", module = "policy")
    }

    implementation(tjenestespesifikasjon("person-v3-tjenestespesifikasjon"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion") {
        exclude(group = "junit")
        exclude(group = "org.eclipse.jetty", module = "jetty-server")
    }
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")

    testImplementation("io.cucumber:cucumber-junit:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-java8:$cucumberVersion")
    testImplementation("com.tngtech.archunit:archunit:$archUnitVersion")
    testImplementation("com.tngtech.archunit:archunit-junit5:$archUnitVersion")

    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}


wsdl2java {
    wsdlDir = file("$projectDir/src/main/wsdl")
    wsdlsToGenerate = listOf(
        listOf("$wsdlDir/MT_1067_NAV_v1.wsdl")
    )
}


allprojects {
        repositories {
            jcenter()
            mavenCentral()
            maven("https://dl.bintray.com/kotlin/ktor")
            maven("https://kotlin.bintray.com/kotlinx")
            maven("https://jitpack.io")
            maven {
            url = uri("https://maven.pkg.github.com/navikt/tjenestespesifikasjoner")
            credentials {
            username = githubUser
            password = githubPassword }
    }
}



tasks.withType<Wrapper> {
    gradleVersion = "6.6"
    }
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}








