package no.nav.medlemskap.modell.medl

import java.time.LocalDate
import java.time.LocalDateTime

data class Medlemskapsunntak (
        val dekning: String?,
        val fraOgMed: LocalDate,
        val grunnlag: String,
        val helsedel: Boolean,
        val ident: String,
        val lovvalg: String,
        val lovvalgsland: String?,
        val medlem: Boolean,
        val status: String,
        val statusaarsak: String?,
        val tilOgMed: LocalDate,
        val untakId: Int,
        val sporingsinformasjon: Sporingsinformasjon?,
        val studieinformasjon: Studieinformasjon?
)

data class Sporingsinformasjon (
        val besluttet: LocalDate?,
        val kilde: String,
        val kildedokument: String?,
        val opprettet: LocalDateTime,
        val opprettetAv: String,
        val registrert: LocalDate?,
        val sistEndret: LocalDateTime,
        val sistEndretAv: String,
        val versjon: String
)

data class Studieinformasjon (
        val delstudie: Boolean,
        val soeknadInnvilget: Boolean,
        val statsborgerland: String,
        val studieland: String?
)

