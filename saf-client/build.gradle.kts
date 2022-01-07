val graphqlKotlinClientVersion = "4.2.0"
val coroutinesVersion = "1.5.2"

plugins {
    kotlin("jvm")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlKotlinClientVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
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