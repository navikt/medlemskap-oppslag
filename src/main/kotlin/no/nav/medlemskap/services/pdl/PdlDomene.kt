package no.nav.medlemskap.services.pdl



data class HentIdenterResponse(val data: Data, val errors: List<PdlError>?)

data class Data(val hentIdenter: Identliste?)

data class Identliste(val identer: List<IdentInformasjon>)

data class IdentInformasjon(
        val ident: String,
        val historisk: Boolean,
        val gruppe: IdentGruppe
)

enum class IdentGruppe {
    FOLKEREGISTERIDENT, NPID, AKTORID
}

data class GraphqlQuery(
        val query: String,
        val variables: Variables
)

data class PersonGraphqlQuery(
        val query: String,
        val variables: BostedVariabler
)

data class BostedVariabler(
        val ident: String,
        val bostedHistorikk: Boolean
)

data class Variables(
        val ident: String,
        val navnHistorikk: Boolean,
        val grupper: List<IdentGruppe> = listOf(IdentGruppe.AKTORID, IdentGruppe.FOLKEREGISTERIDENT, IdentGruppe.NPID)
)

data class PdlError(
        val message: String,
        val locations: List<PdlErrorLocation>,
        val path: List<String>?,
        val extensions: PdlErrorExtension
)

data class PdlErrorLocation(
        val line: Int?,
        val column: Int?
)

data class PdlErrorExtension(
        val code: String?,
        val classification: String
)

fun hentIndenterQuery(fnr: String): GraphqlQuery {
    val query = GraphqlQuery::class.java.getResource("/pdl/hentIdenter.graphql").readText().replace("[\n\r]", "")
    return GraphqlQuery(query, Variables(fnr, false))
}


fun hentPersonQuery(fnr: String): PersonGraphqlQuery{
    val query = GraphqlQuery::class.java.getResource("/pdl/hentPerson.graphql").readText().replace("[\n\r]", "")
    return PersonGraphqlQuery(query, BostedVariabler(fnr, true))
}

fun hentNasjonalitetQuery(fnr: String): GraphqlQuery {
    val query = GraphqlQuery::class.java.getResource("/pdl/hentNasjonalitet.graphql").readText().replace("[\n\r]", "")
    return GraphqlQuery(query, Variables(fnr, false))
}

