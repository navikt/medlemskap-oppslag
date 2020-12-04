import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
// Temporary to fix high severity Snyk vulernabilities:
val nettyCodecVersion = "4.1.54.Final"
val commonsCodecVersion = "3.2.2"
val httpClientVersion = "4.5.13"
val jettyWebAppVersion = "9.4.35.v20201120"

val mainClass = "no.nav.medlemskap.ApplicationKt"

fun tjenestespesifikasjon(name: String) = "no.nav.tjenestespesifikasjoner:$name:$tjenestespesifikasjonerVersion"

plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("com.expediagroup.graphql") version "3.6.1" apply false
    id("com.github.ben-manes.versions") version "0.29.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
    id("org.jlleitschuh.gradle.ktlint-idea") version "9.3.0"
    id("org.hidetake.swagger.generator") version "2.18.2" apply true
}

val githubUser: String by project
val githubPassword: String by project

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
                password = githubPassword
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.withType<Wrapper> {
        gradleVersion = "6.6"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(path = ":pdl-client", configuration = "archives"))
    implementation(project(path = ":saf-client", configuration = "archives"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion") {
        exclude(group = "io.netty", module = "netty-codec")
        exclude(group = "io.netty", module = "netty-codec-http")
    }
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:latest.release")
    implementation("io.micrometer:micrometer-registry-influx:latest.release")
    implementation("io.prometheus:simpleclient_hotspot:$prometheusVersion")
    implementation("io.prometheus:simpleclient_common:$prometheusVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("com.natpryce:konfig:$konfigVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggerVersion")
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
    implementation("io.github.resilience4j:resilience4j-retry:$resilience4jVersion")
    implementation("io.github.resilience4j:resilience4j-kotlin:$resilience4jVersion")
    implementation("no.bekk.bekkopen:nocommons:$nocommonsVersion")
    implementation("com.expediagroup:graphql-kotlin-client:$graphqlKotlinClientVersion")
    implementation("com.neovisionaries:nv-i18n:$nvi18nVersion")
    swaggerUI("org.webjars:swagger-ui:$swaggerUiVersion")

    // Temporary to fix high severity Snyk vulernabilities:
    implementation("io.netty:netty-codec:$nettyCodecVersion")
    implementation("io.netty:netty-codec-http:$nettyCodecVersion")
    implementation("commons-collections:commons-collections:$commonsCodecVersion")
    implementation("org.apache.httpcomponents:httpclient:$httpClientVersion")

    // Override versions to fix high and medium severity Snyk vaulnerabilities
    testImplementation("org.eclipse.jetty:jetty-webapp:$jettyWebAppVersion")

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
    testImplementation("org.skyscreamer:jsonassert:$jsonassertVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    testImplementation("com.atlassian.oai:swagger-request-validator-core:$swaggerRequestValidatorVersion") {
        exclude(group = "com.fasterxml.jackson.dataformat", module = "jackson-dataformat-yaml")
    }
    testImplementation("com.atlassian.oai:swagger-request-validator-restassured:$swaggerRequestValidatorVersion")

    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

swaggerSources {
    create("lovme-api").apply {
        setInputFile(file("src/main/resources/lovme.yaml"))
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn("ktlintFormat")
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    withType<org.hidetake.gradle.swagger.generator.GenerateSwaggerUI> {
        outputDir = File(buildDir.path + "/resources/main/api")
    }

    withType<ShadowJar> {
        dependsOn("generateSwaggerUI")
        archiveBaseName.set("app")
        archiveClassifier.set("")
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to mainClass
                )
            )
        }
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
    }
}

ktlint {
    disabledRules.set(setOf("no-wildcard-imports"))
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
