val graphqlKotlinClientVersion = "5.2.0"
val coroutinesVersion = "1.5.2"

plugins {
    kotlin("jvm")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlKotlinClientVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:1.6.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.1")
}

graphql {
    client {
        sdlEndpoint = "https://navikt.github.io/saf/saf-api-sdl.graphqls"
        packageName = "no.nav.medlemskap.clients.saf.generated"
        allowDeprecatedFields = false
        //clientType = com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType.KTOR
        queryFiles = listOf<File>(file("${project.projectDir}/src/main/resources/saf/dokumenter.graphql"))
    }
}