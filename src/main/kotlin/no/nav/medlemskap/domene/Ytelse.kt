package no.nav.medlemskap.domene

enum class Ytelse {
    SYKEPENGER,
    DAGPENGER,
    ENSLIG_FORSORGER,
    LOVME,
    LOVME_GCP,
    LOVME_FUNKSJONELLE_TESTER;

    companion object {

        val clientIdToYtelseMap: Map<String, Ytelse> = hashMapOf(
            "a16f1075-4482-4577-baab-d2a7323655fa" to SYKEPENGER, // Prod (skal utgå) - sparkel-medlemskap
            "2a9e52c1-a4f5-41cf-a5ce-b9ec9e83b588" to SYKEPENGER, // Prod:tbd - sparkel-medlemskap
            "4487496e-9ba3-4c3a-a083-f45ca3878775" to SYKEPENGER, // dev
            "337777ed-eced-4765-9504-c6e7aebec9e5" to DAGPENGER, // Prod - dp-oppslag-medlemskap
            "1e8e8e29-a308-455f-b744-adcb4d2fe763" to DAGPENGER, // dev
            "4de2bae4-f86a-4c12-aeef-74177c6e724a" to ENSLIG_FORSORGER, // Prod - familie-ef-soknad-api
            "ea03f30a-413b-4f0e-bfae-477aad57de67" to ENSLIG_FORSORGER, // dev
            "496b0ded-cfdb-4430-b7b6-b568504de005" to LOVME, // dev
            "2719da58-489e-4185-9ee6-74b7e93763d2" to LOVME_GCP, // dev
            "d24ecfa9-ffa7-48dc-a9f3-f8ae3138e603" to LOVME_FUNKSJONELLE_TESTER // dev
        )

        fun fromClientId(clientId: String?): Ytelse? = clientIdToYtelseMap[clientId]
        fun toMedlemskapClientId(): String = clientIdToYtelseMap.filter { it.value == LOVME }.keys.first()
        fun Ytelse.name(): String = this.name.toLowerCase().capitalize()
    }
}
