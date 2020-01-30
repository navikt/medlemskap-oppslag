package no.nav.medlemskap.modell.pdl


data class HentIdenterResponse(val data: Data)

data class Data(val hentIdenter: Identliste)

data class Identliste(val identer: List<IdentInformasjon>)

data class IdentInformasjon(val ident: String, val historisk: Boolean, val type: IdentGruppe)

enum class IdentGruppe {
    FOLKEREGISTERIDENT, NPID, AKTORID
}


open class GraphqlQuery(val query: String, val variables: String)

data class HentIdenterQuery(val fnr: String) : GraphqlQuery(
        query = """ 
            query(${'$'}ident: ID!, ${'$'}grupper: [IdentGruppe!], ${'$'}historikk: Boolean = false) {
                hentIdenter(ident: ${'$'}ident, grupper:${'$'}grupper, historikk:${'$'}historikk) {
                    identer {
                        ident,
                        historisk,
                        gruppe
                    }
                }
            }
            """.trimIndent(),
        variables = """
            {
                  "ident":"$fnr",
                  "historikk": true,
                  "grupper": ["${IdentGruppe.FOLKEREGISTERIDENT}", "${IdentGruppe.AKTORID}", "${IdentGruppe.NPID}"]
              }
        """.trimIndent()
)