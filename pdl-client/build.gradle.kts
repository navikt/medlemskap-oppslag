val graphqlKotlinClientVersion = "4.0.0-alpha.12"
val coroutinesVersion = "1.6.0"

plugins {
    kotlin("jvm")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlKotlinClientVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
}

tasks.withType<com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask> {
    packageName.set("no.nav.medlemskap.clients.pdl.generated")
    schemaFile.set(file("${project.projectDir}/src/main/resources/pdl/pdl-api-sdl.graphqls"))
    queryFileDirectory.set("${project.projectDir}/src/main/resources/pdl")
    clientType.set(com.expediagroup.graphql.plugin.gradle.config.GraphQLClientType.KTOR)
}