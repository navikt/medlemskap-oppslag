package no.nav.medlemskap.domene

enum class Ytelse {
    SYKEPENGER,
    DAGPENGER,
    ENSLIG_FORSORGER;

    companion object {

        val clientIdToYtelseMap: Map<String, Ytelse> = hashMapOf(
                "a16f1075-4482-4577-baab-d2a7323655fa" to SYKEPENGER,
                "337777ed-eced-4765-9504-c6e7aebec9e5" to DAGPENGER, //Prod
                "1e8e8e29-a308-455f-b744-adcb4d2fe763" to DAGPENGER, //dev
                "4de2bae4-f86a-4c12-aeef-74177c6e724a" to ENSLIG_FORSORGER
        )

        fun fromClientId(clientId: String?): Ytelse? = clientIdToYtelseMap[clientId]
    }
}