val graphqlKotlinClientVersion = "3.6.1"

plugins {
    kotlin("jvm")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.expediagroup:graphql-kotlin-client:$graphqlKotlinClientVersion")
}

graphql {
    client {
        sdlEndpoint = "https://navikt.github.io/saf/saf-api-sdl.graphqls"
        packageName = "no.nav.medlemskap.clients.saf.generated"
        allowDeprecatedFields = false
        queryFiles.add(file("${project.projectDir}/src/main/resources/saf/dokumenter.graphql"))
    }
}

