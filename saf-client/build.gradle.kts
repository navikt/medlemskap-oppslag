val graphqlKotlinClientVersion = "4.0.0-alpha.12"

plugins {
    kotlin("jvm")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlKotlinClientVersion")
}

graphql {
    client {
        sdlEndpoint = "https://navikt.github.io/saf/saf-api-sdl.graphqls"
        packageName = "no.nav.medlemskap.clients.saf.generated"
        allowDeprecatedFields = false
        clientType = com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType.KTOR
        queryFiles = listOf<File>(file("${project.projectDir}/src/main/resources/saf/dokumenter.graphql"))
    }
}