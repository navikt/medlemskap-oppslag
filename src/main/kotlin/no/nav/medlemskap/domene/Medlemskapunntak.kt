package no.nav.medlemskap.domene

import java.time.LocalDate

data class Medlemskapsunntak (
        val dekning: String?,
        val fraOgMed: LocalDate,
        val tilOgMed: LocalDate,
        val erMedlem: Boolean,
        val lovvalg: String,
        val lovvalgland: String?
)
