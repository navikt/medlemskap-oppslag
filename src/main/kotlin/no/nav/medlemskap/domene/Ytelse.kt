package no.nav.medlemskap.domene

enum class Ytelse(val clientId: String) {
    SYKEPENGER("a16f1075-4482-4577-baab-d2a7323655fa"),
    DAGPENGER("337777ed-eced-4765-9504-c6e7aebec9e5"),
    ENSLIG_FORSORGER("4de2bae4-f86a-4c12-aeef-74177c6e724a");

    companion object {
        private val map = values().associateBy(Ytelse::clientId)
        fun fromClientId(clientId: String?): Ytelse? = map[clientId]
    }
}