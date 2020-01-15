package no.nav.medlemskap.modell.medl

data class Medlemskapsunntak (
        val dekning: String?,
        val fraOgMed: String, // ISO-8601 dato
        val grunnlag: String,
        val helsedel: Boolean,
        val ident: String,
        val lovvalg: String,
        val lovvalgsland: String?,
        val medlem: Boolean,
        val status: String,
        val statusaarsak: String?,
        val tilOgMed: String, // ISO-8601 dato
        val untakId: Int,
        val sporingsinformasjon: Sporingsinformasjon?,
        val studieinformasjon: Studieinformasjon?
)

data class Sporingsinformasjon (
        val besluttet: String, // ISO-8601 dato
        val kilde: String,
        val opprettet: String, // ISO-8601 dato-tid
        val opprettetAv: String,
        val registrert: String, // ISO-8601 dato
        val sistEndret: String, // ISO-8601 dato-tid
        val sistEndretAv: String,
        val versjon: String
)

data class Studieinformasjon (
        val delstudie: Boolean,
        val soeknadInnvilget: Boolean,
        val statsborgerland: String,
        val studieland: String
)
