package no.nav.medlemskap.modell.pdl


data class HentIdenterResponse(val data: Data)

data class Data(val hentIdenter: Identliste)

data class Identliste(val identer: List<IdentInformasjon>)

data class IdentInformasjon(val ident: String, val historisk: Boolean, val type: IdentGruppe)

enum class IdentGruppe {
    FOLKEREGISTERIDENT, NPID, AKTORID
}

open class GraphqlQuery(val query: String, val variables: Variables)

data class Variables(val ident: String, val navnHistorikk: Boolean = false, val grupper: List<IdentGruppe> = listOf(IdentGruppe.AKTORID, IdentGruppe.FOLKEREGISTERIDENT, IdentGruppe.NPID))


fun hentIndenterQuery(fnr: String): GraphqlQuery {
    val query = GraphqlQuery::class.java.getResource("/pdl/hentIdenter.graphql").readText().replace("[\n\r]", "")
    return GraphqlQuery(query, Variables(fnr))
}
