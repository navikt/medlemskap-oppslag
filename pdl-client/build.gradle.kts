val graphqlKotlinClientVersion = "3.6.2"

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
        sdlEndpoint = "https://navikt.github.io/pdl/pdl-api-sdl.graphqls"
        packageName = "no.nav.medlemskap.clients.pdl.generated"
        allowDeprecatedFields = false

        queryFiles = mutableListOf(
                file("${project.projectDir}/src/main/resources/pdl/hentIdenter.graphql"),
                file("${project.projectDir}/src/main/resources/pdl/hentPerson.graphql")
        )
    }
}


