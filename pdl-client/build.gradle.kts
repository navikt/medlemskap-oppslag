
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:1.6.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.1")
}

val graphqlGenerateClient by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask::class) {
    packageName.set("no.nav.medlemskap.clients.pdl.generated")
    schemaFile.set(file("${project.projectDir}/src/main/resources/pdl/pdl-api-sdl.graphqls"))
    queryFileDirectory.set("${project.projectDir}/src/main/resources/pdl")
    serializer.set(GraphQLSerializer.JACKSON)
}